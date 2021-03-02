pConfig
======

This project is a configuration API, for complex projects with many separated parts which may each require their own configuration section and pieces.

This allows them to define a configuration layout using simple Java objects and allows the library to do all the grunt-work of converting to YAML and back, while also allowing in-game configuration for most (simple) objects.
 
You simply request a root config manager, and you are then able to create and manage sub-nodes as well as regular nodes, which are generic objects that allow you to store any object or collection you wish.

Most objects you will store are already handled for you, boxed primitives, collections etc. 
However you may also register your own object types, simply using the Bukkit API.
(See https://www.spigotmc.org/threads/148781/)

Installation
-------
Simply clone this repository, install it using maven using `clean install`, and shade it into your project.
```xml
<dependency>
    <groupId>dev.oscaz</groupId>
    <artifactId>pConfig</artifactId>
    <version>1.0</version>
    <scope>compile</scope>
</dependency>
```

Usage
-------
```java
RootConfigManager configManager = DynamicConfigurationManager.get(this); // Instantiate your config-manager, passing in your plugin instance

ConfigNode<Double> decimal_node = configManager.manage("test_double", 20.0); // Define a key and a default value
ConfigNode<String> string_node = configManager.manage("test_string", "testing123"); // Define a key and a default value
ConfigNode<List<Integer>> list_integer_node = configManager.manage("test_list_integer", Lists.newArrayList(1, 2, 10)); // Define a key and a default value

SubConfigNode subNode = configManager.manageSubNode("test_subnode");
ConfigNode<Float> float_node = subNode.manage("test_float", 10.0f); // Define a key and a default value
```

In-game GUI
```java
@Override
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) return false;
    if (!sender.isOp()) return false;
    
    this.configManager.getGUI().open((Player) sender);
    
    return false;
}
```

![Demo](https://i.gyazo.com/590a5e16fc09c89782702b355d24b262.gif)

Features & Bugs
-----

If you have a feature request, or a bug to report, please open an issue.\
If you are a developer and wish to add additional functionality, please open a pull request.