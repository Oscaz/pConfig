package dev.oscaz.pconfig;

import com.cryptomorin.xseries.XMaterial;
import dev.oscaz.pconfig.api.ConfigNode;
import dev.oscaz.pconfig.api.ConfigurationGUI;
import dev.oscaz.pconfig.api.RootConfigManager;
import dev.oscaz.pconfig.api.SubConfigNode;
import dev.oscaz.pconfig.util.DecimalFormatType;
import dev.oscaz.pconfig.util.ItemUtil;
import dev.oscaz.pconfig.util.MessageUtil;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryListener;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.javatuples.Triplet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommonConfigurationGUI implements ConfigurationGUI, Listener {
	
	private final RootConfigManager configManager;
	private final SmartInventory inventory;
	private final Map<UUID, Stack<SubConfigNode>> nodeView;
	private final Map<UUID, Integer> page;
	private final Map<UUID, Triplet<InputType, ConfigNode<?>, Boolean>> setting;
	
	protected CommonConfigurationGUI(RootConfigManager configManager) {
		this.configManager = configManager;
		this.nodeView = Maps.newHashMap();
		this.page = Maps.newHashMap();
		this.setting = Maps.newHashMap();
		this.inventory = SmartInventory.builder()
				.size(6, 9)
				.provider(this)
				.type(InventoryType.CHEST)
				.id(configManager.getPlugin().getName() + "-config")
				.title(configManager.getPlugin().getName() + " Config")
				.manager(this.configManager.getInventoryManager())
				.listener(new InventoryListener<>(InventoryCloseEvent.class, event -> {
					if (this.setting.containsKey(event.getPlayer().getUniqueId())) return;
					Stack<SubConfigNode> nodes = this.nodeView.get(event.getPlayer().getUniqueId());
					if (nodes == null) return;
					if (nodes.isEmpty()) { // in root node
						this.nodeView.remove(event.getPlayer().getUniqueId());
						return;
					} else {
						nodes.pop();
						if (nodes.isEmpty()) {
							return;
						} else {
							this.page.remove(event.getPlayer().getUniqueId());
							Bukkit.getScheduler().runTaskLater(this.configManager.getPlugin(), () -> {
								this.openVar((Player) event.getPlayer());
							}, 1L);
						}
					}
				}))
				.build();
		Bukkit.getPluginManager().registerEvents(this, configManager.getPlugin());
	}
	
	@Override
	public void open(Player player) {
		Stack<SubConfigNode> stack = new Stack<>();
		stack.push(this.configManager.getRootNode());
		this.nodeView.put(player.getUniqueId(), stack);
		this.inventory.open(player);
	}
	
	private void openVar(Player player) {
		this.inventory.open(player);
	}
	
	private static final ClickableItem PANE_ITEM = ClickableItem.empty(
			ItemUtil.modifyItem(
					XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(true),
					""
			)
	);
	
	@Override
	public void init(Player player, InventoryContents contents) {
		contents.fill(null);
		Stack<SubConfigNode> stack = this.nodeView.get(player.getUniqueId());
		int page = this.page.getOrDefault(player.getUniqueId(), 0);
		int maxPage = (int) Math.floor((double) stack.peek().getChildren() / 45);
		
		List<String> nodeTree = stack.stream()
				.map(SubConfigNode::getKey)
				.collect(Collectors.toList());
		IntStream.range(0, nodeTree.size()).forEach(i -> {
			StringBuilder nodeTreeString = new StringBuilder(nodeTree.get(i));
			nodeTreeString.insert(0, "&f");
			for (int dash = 0; dash < i; dash++) {
				nodeTreeString.insert(0, "&7-&f");
			}
			nodeTree.set(i, nodeTreeString.toString());
		});
		nodeTree.set(0, "&fRoot");
		nodeTree.addAll(Arrays.asList("", "&7Click to return to root."));
		
		contents.fillRow(0, PANE_ITEM);
		contents.set(SlotPos.of(0,0), ClickableItem.of(
				ItemUtil.createItem(
						XMaterial.PAPER.parseMaterial(true),
						"&aCurrent Node",
						nodeTree
				), event -> {
					stack.clear();
					stack.push(this.configManager.getRootNode());
					this.page.remove(player.getUniqueId());
					this.init(player, contents);
				}
		));
		contents.set(SlotPos.of(0,8), ClickableItem.of(
				ItemUtil.createItem(
						XMaterial.BARRIER.parseMaterial(true),
						"&cClose"
				), event -> {
					this.page.remove(player.getUniqueId());
					this.nodeView.remove(player.getUniqueId());
					player.closeInventory();
				}
		));
		contents.set(SlotPos.of(0,3), ClickableItem.of(
				ItemUtil.createItem(
						XMaterial.ARROW.parseMaterial(true),
						"&aBack",
						"&7(" + (page + 1) + "/" + (maxPage + 1) + ")"
				), event -> {
					if (page - 1 < 0) {
						return;
					}
					this.page.put(player.getUniqueId(), page - 1);
					this.init(player, contents);
				}
		));
		contents.set(SlotPos.of(0,4), ClickableItem.of(
				ItemUtil.createItem(
						XMaterial.CLOCK.parseMaterial(true),
						"&aReload Configuration",
						"&cWarning: &7This will forcibly load",
						"&7configuration from your configuration file.",
						"&7This can cause issues if you are currently",
						"&7uploading or editing this file."
				), event -> {
					this.page.remove(player.getUniqueId());
					this.nodeView.remove(player.getUniqueId());
					player.closeInventory();
					long nanos = System.nanoTime();
					this.configManager.reload();
					nanos = System.nanoTime() - nanos;
					MessageUtil.sendMessage(player, "&a&l(&f!&a&l) &aSuccessfully reloaded configuration. &7[" + DecimalFormatType.LOCATION.format(nanos / 1_000_000) + "ms]");
				}
		));
		contents.set(SlotPos.of(0,5), ClickableItem.of(
				ItemUtil.createItem(
						XMaterial.ARROW.parseMaterial(true),
						"&aForward",
						"&7(" + (page + 1) + "/" + (maxPage + 1) + ")"
				), event -> {
					if (page + 1 > maxPage) {
						return;
					}
					this.page.put(player.getUniqueId(), page + 1);
					this.init(player, contents);
				}
		));
		
		List<Object> foundNodes = Lists.newArrayList();
		
		SubConfigNode node = stack.peek();
		List<SubConfigNode> subNodes = node.getSubNodes().stream()
				.sorted(Comparator.comparing(SubConfigNode::getKey))
				.collect(Collectors.toList());
		foundNodes.addAll(subNodes);
		
		List<ConfigNode<?>> nodes = node.getNodes().stream()
				.sorted(Comparator.comparing(ConfigNode::getKey))
				.collect(Collectors.toList());
		foundNodes.addAll(nodes);
		
		int indexFrom = page * 45;
		int indexTo = page * 45 + 45;
		
		for (int i = indexFrom; i < indexTo; i++) {
			try {
				Object unknownNode = foundNodes.get(i);
				if (unknownNode instanceof SubConfigNode) {
					SubConfigNode subNode = (SubConfigNode) unknownNode;
					contents.add(ClickableItem.of(
							ItemUtil.createItem(
									XMaterial.CHEST_MINECART.parseMaterial(true),
									"&a" + subNode.getKey(),
									"&aChildren: &f" + subNode.getChildren(),
									"&a&nClick to view"
							), event -> {
								this.page.remove(player.getUniqueId());
								this.nodeView.get(player.getUniqueId()).push(subNode);
								this.init(player, contents);
							}
					));
				} else if (unknownNode instanceof ConfigNode) {
					ConfigNode<?> configNode = (ConfigNode<?>) unknownNode;
					contents.add(ClickableItem.of(
							ItemUtil.createItem(
									XMaterial.PAPER.parseMaterial(true),
									"&a" + configNode.getKey(),
									"&aValue: &f" + configNode.getReal().toString(),
									"&aTemporary: &f" + (configNode.getTemporary() == null ? "None" : configNode.getTemporary()),
									"",
									"&aLeft-click to edit",
									"&aRight-click to edit temporary",
									"&aMiddle-click to remove temporary"
							), event -> {
								if (event.getClick() == ClickType.MIDDLE) {
									configNode.setTemporary(null);
									this.init(player, contents);
									return;
								}
								
								Optional<InputType> inputType = configNode.getInputType();
								if (!inputType.isPresent()) {
									MessageUtil.sendMessage(player, "&c&l(!) &cSorry, but setting in that format is not available yet, please set via the configuration file.");
									return;
								}
								InputType type = inputType.get();
								MessageUtil.sendMessage(player, type.getMessage().replace("%node%", configNode.getKey()));
								this.setting.put(player.getUniqueId(), Triplet.with(type, configNode, event.getClick().isRightClick()));
								this.inventory.close(player);
							}
					));
				}
			} catch (IndexOutOfBoundsException e) {
				break;
			}
		}
	}
	
	@Override
	public void update(Player player, InventoryContents contents) {
	
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void on(AsyncPlayerChatEvent event) {
		if (!this.setting.containsKey(event.getPlayer().getUniqueId())) return;
		event.setCancelled(true);
		
		String message = event.getMessage();
		
		Bukkit.getScheduler().runTask(this.configManager.getPlugin(), () -> {
			Triplet<InputType, ConfigNode<?>, Boolean> setting = this.setting.get(event.getPlayer().getUniqueId());
			this.attemptSet(event.getPlayer(), setting, message);
			this.configManager.save();
			this.setting.remove(event.getPlayer().getUniqueId());
			this.inventory.open(event.getPlayer());
		});
	}
	
	private boolean attemptSet(Player player, Triplet<InputType, ConfigNode<?>, Boolean> setting, String message) {
		InputType inputType = setting.getValue0();
		ConfigNode<?> node = setting.getValue1();
		boolean temporary = setting.getValue2();
		Optional<?> parsed = inputType.parse(player, message);
		
		if (!parsed.isPresent()) {
			MessageUtil.sendMessage(player, "&c&l(!) &cThere was an error while attempting to parse your input! Please make sure it is valid!");
			return false;
		}
		
		switch (inputType) {
			case BYTE:
				ConfigNode<Byte> castByteNode = (ConfigNode<Byte>) node;
				if (temporary) castByteNode.setTemporary((Byte) parsed.get());
				else castByteNode.set((Byte) parsed.get());
				return true;
			case SHORT:
				ConfigNode<Short> castShortNode = (ConfigNode<Short>) node;
				if (temporary) castShortNode.setTemporary((Short) parsed.get());
				else castShortNode.set((Short) parsed.get());
				return true;
			case INTEGER:
				ConfigNode<Integer> castIntNode = (ConfigNode<Integer>) node;
				if (temporary) castIntNode.setTemporary((Integer) parsed.get());
				else castIntNode.set((Integer) parsed.get());
				return true;
			case LONG:
				ConfigNode<Long> castLongNode = (ConfigNode<Long>) node;
				if (temporary) castLongNode.setTemporary((Long) parsed.get());
				else castLongNode.set((Long) parsed.get());
				return true;
			case FLOAT:
				ConfigNode<Float> castFloatNode = (ConfigNode<Float>) node;
				if (temporary) castFloatNode.setTemporary((Float) parsed.get());
				else castFloatNode.set((Float) parsed.get());
				return true;
			case DOUBLE:
				ConfigNode<Double> castDoubleNode = (ConfigNode<Double>) node;
				if (temporary) castDoubleNode.setTemporary((Double) parsed.get());
				else castDoubleNode.set((Double) parsed.get());
				return true;
			case STRING:
				ConfigNode<String> castStringNode = (ConfigNode<String>) node;
				if (temporary) castStringNode.setTemporary((String) parsed.get());
				else castStringNode.set((String) parsed.get());
				return true;
			case CHARACTER:
				ConfigNode<Character> castCharacterNode = (ConfigNode<Character>) node;
				if (temporary) castCharacterNode.setTemporary((Character) parsed.get());
				else castCharacterNode.set((Character) parsed.get());
				return true;
			case LOCATION:
				ConfigNode<Location> castLocationNode = (ConfigNode<Location>) node;
				if (temporary) castLocationNode.setTemporary((Location) parsed.get());
				else castLocationNode.set((Location) parsed.get());
		}
		return false;
	}

}
