package com.example.demo.proyectoAddons.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.proyectoAddons.model.Addon;
import com.example.demo.proyectoAddons.model.Administrador;
import com.example.demo.proyectoAddons.model.Archivo;
import com.example.demo.proyectoAddons.model.Creador;
import com.example.demo.proyectoAddons.model.Usuario;
import com.example.demo.proyectoAddons.repository.AddonRepository;
import com.example.demo.proyectoAddons.repository.AdministradorRepository;
import com.example.demo.proyectoAddons.repository.ArchivoRepository;
import com.example.demo.proyectoAddons.repository.CreadorrRepository;
import com.example.demo.proyectoAddons.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private CreadorrRepository creadorRepository;

    @Autowired
    private AddonRepository addonRepository;

    @Autowired
    private ArchivoRepository archivoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:admin@admin.com}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:12345678}")
    private String adminPassword;

    @Value("${ADMIN_NAME:admin}")
    private String adminName;

    @Value("${ADDONS_PREVIOS:no}")
    private String addonsPrevios;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Usuario adminUsuario = usuarioRepository.findByEmail(adminEmail).orElse(null);

        if (administradorRepository.count() == 0) {
            System.out.println("No se detectó ningún administrador. Creando administrador por defecto...");

            if (adminUsuario == null) {
                adminUsuario = new Usuario();
                adminUsuario.setNombre(adminName);
                adminUsuario.setEmail(adminEmail);
                adminUsuario.setPassword(passwordEncoder.encode(adminPassword));
                adminUsuario.setEsDePago(true);
                adminUsuario = usuarioRepository.save(adminUsuario);
                System.out.println("Usuario admin creado: " + adminEmail);
            } else {
                System.out.println("El usuario para el administrador ya existe.");
            }

            Administrador admin = new Administrador();
            admin.setUsuario(adminUsuario);
            admin.setId(adminUsuario.getId());
            administradorRepository.save(admin);
            System.out.println("Rol de Administrador asignado al usuario: " + adminEmail);
        } else {
            System.out.println("Administrador ya creado.");
        }


        if (adminUsuario != null && !creadorRepository.existsById(adminUsuario.getId())) {
            Creador adminCreador = new Creador();
            adminCreador.setId(adminUsuario.getId());
            adminCreador.setUsuario(adminUsuario);
            adminCreador.setEspecialidad("Administrador y Creador");
            creadorRepository.save(adminCreador);
            creadorRepository.flush(); // Forzar!
        }

        if ("si".equalsIgnoreCase(addonsPrevios) && addonRepository.count() == 0) {

            Addon modernFurniture = new Addon();
            modernFurniture.setNombre("Modern Furniture");
            modernFurniture.setTipo("add-on");
            modernFurniture.setTag("Decoracion");
            modernFurniture.setUrlMiniatura("https://www.trmc-addons.com/add-ons/modern-furniture/thumbnail.webp");
            modernFurniture.setDescripcion("Modern Furniture is an Add-On for Minecraft Bedrock designed for decorating houses, buildings, and modern streets. This Add-On adds over 800 furniture pieces/blocks, categorized into different boxes such as the sofa box, armchair box, construction box, and more.");
            modernFurniture.setTextoAddon("<div><p>Modern Furniture is an Add-On for Minecraft Bedrock designed for decorating houses, buildings, and modern streets. This Add-On adds over <strong>800 furniture</strong> pieces/blocks, categorized into different boxes such as the sofa box, armchair box, construction box, and more.</p></div>\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "<div><a href=\"https://www.trmc-addons.com/add-ons/modern-furniture/\" target=\"_blank\" rel=\"nofollow\"><img style=\"display:block;margin-left:auto;margin-right:auto\" src=\"https://www.trmc-addons.com/assets/download.png\" width=\"629\" height=\"190\"></a></div>\n" +
                    "\n" +
                    "<div><br><p>🛋 <strong>Sofas:</strong>&nbsp;Fully functional and modularly arranged<br><br>📚&nbsp;<strong>Bookshelves:</strong>&nbsp;Apart from being decorative and having different types you can modify them to change variations of the books.<br><br>🌸&nbsp;<strong>Decorations:</strong>&nbsp;Hundreds of decorations of all kinds such as plants, pots, books, statues, utensils and much more!<br><br>🍴&nbsp;<strong>Kitchen Furniture:</strong>&nbsp;Functional and modular kitchen cabinets and countertops.<br><br>💡&nbsp;<strong>Lamps:</strong>&nbsp;Fully functional lights and switches can be turned on/off by clicking with the hand.<br><br>🍽️&nbsp;<strong>Tables:</strong>&nbsp;Hundreds of variations of both color and material.<br><br>🪑&nbsp;<strong>Chairs:</strong>&nbsp;Fully functional chairs that allow you to sit with a single click.<br><br>🧩&nbsp;<strong>Ducts:</strong>&nbsp;Functional ducts and slits that can be opened and closed<br><br>🧶&nbsp;<strong>Carpets:</strong>&nbsp;Rugs with different designs, simple and powerful.<br><br>🚪&nbsp;<strong>Garage Doors:</strong>&nbsp;Functional garage door and able to be operated by one lever.<br><br>🗑️&nbsp;<strong>Trash Bins:</strong>&nbsp;Place a trash bin and throw an item above it to delete the item instantly.<br><br>📅&nbsp;<strong>Calendar:</strong>&nbsp;Keeps track of the days played in your world. Click to view the full timeline of days spent in your adventure!<br><br>🏠&nbsp;<strong>Curtains:</strong>&nbsp;Available in all colors, open and close with a click!</p><p>&nbsp;</p><p>To create the furniture, you’ll need clay balls. You can place them in a stonecutter, and from there, you’ll be able to select all kinds of furniture to decorate your Minecraft world.</p><br><p>The furniture design can be described as having a \"simple\" touch. This means the furniture aims to be minimalist while maintaining a Minecraft-style coherence. Thanks to this consistency, most furniture pieces blend seamlessly with Minecraft’s blocks, allowing for professional-looking decorations.</p><br>\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "# ![](https://www.trmc-addons.com/assets/mcpedl_img/mod1.webp)\n" +
                    "\n" +
                    "**Creative:**\n" +
                    "\n" +
                    "To acquire the furniture in Creative mode, you will find it in the decoration tab of the Creative menu. If you want to obtain it using commands, you will need to type /give [@p](https://github.com/p) f: (then you can add the name of the furniture you want).\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "**Survival:**\n" +
                    "\n" +
                    "To create the furniture, you will need clay and a stonecutter. Then, you will need to place the clay in the stonecutter. A list of boxes will appear—this list represents the furniture categories (such as chairs, sofas, tables, decorations, etc.).\n" +
                    "\n" +
                    "You will have to choose a box. Once selected, you will place it back in the stonecutter, and it will display all the available options. From there, it's up to you to decorate!\n" +
                    "\n" +
                    "![](https://media.forgecdn.net/attachments/description/829201/description_fc74c83c-7fd4-4562-9a6a-ddc2ad449f7e.png)\n" +
                    "\n" +
                    "![](https://media.forgecdn.net/attachments/description/829201/description_51391886-f279-4873-8bcb-a09da16476ff.png)\n" +
                    "\n" +
                    "**Modern Tool:**\n" +
                    "\n" +
                    "The&nbsp;**Modern Tool**&nbsp;is used to modify the geometry of blocks. If you want to know which blocks are&nbsp;**interactive**, they will have the label&nbsp;**\"Configurable\"**&nbsp;and a&nbsp;**wrench symbol**.\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "Clicking on them will change their geometry or a specific aspect of the block. If you want to revert it to its&nbsp;**original state**, you can either&nbsp;**break and replace**&nbsp;the block or click it a few more times until it returns to its original form.\n" +
                    "\n" +
                    "![](https://media.forgecdn.net/attachments/description/829201/description_99456f9c-142e-4068-a39a-9996e56c6b87.png)\n" +
                    "\n" +
                    "# ![](https://www.trmc-addons.com/assets/mcpedl_img/mod2.webp)\n" +
                    "\n" +
                    "![Add-on Image 3](https://www.trmc-addons.com/add-ons/modern-furniture/images/3.webp)\n" +
                    "\n" +
                    "![Add-on Image 4](https://www.trmc-addons.com/add-ons/modern-furniture/images/4.webp)\n" +
                    "\n" +
                    "# ![Add-on Image 1](https://www.trmc-addons.com/add-ons/modern-furniture/images/1.webp)\n" +
                    "\n" +
                    "![Add-on Image 2](https://www.trmc-addons.com/add-ons/modern-furniture/images/2.webp)\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "[![](https://www.trmc-addons.com/assets/mcpedl_img/patreon1.webp)](https://www.patreon.com/cw/trotamundos872)\n" +
                    "\n" +
                    "<p style=\"text-align:center\">[**Support me on Patreon to continue developing my addons!**](https://www.patreon.com/cw/trotamundos872)</p>\n" +
                    "\n" +
                    "[![](https://www.trmc-addons.com/assets/mcpedl_img/patreon2.webp)](https://www.patreon.com/cw/trotamundos872)\n" +
                    "\n" +
                    "[![](https://www.trmc-addons.com/assets/mcpedl_img/patreon_button.webp)](https://www.patreon.com/cw/trotamundos872)\n" +
                    "\n" +
                    "[![](https://www.trmc-addons.com/assets/mcpedl_img/discord1.webp)](https://discord.gg/dKfv3SHpxq)\n" +
                    "\n" +
                    "<p style=\"text-align:center\">[Share your suggestions for creating better add-ons!<span style=\"color:#000\"><img style=\"display:block;margin-left:auto;margin-right:auto\" src=\"https://www.trmc-addons.com/assets/mcpedl_img/discord_button.webp\" width=\"420\" height=\"105\"></span>](https://discord.gg/dKfv3SHpxq)</p>&nbsp;\n" +
                    "\n" +
                    "# Downloads | Official Site\n" +
                    "\n" +
                    "*   [Download Modern Furniture Here | Official Site](https://www.trmc-addons.com/add-ons/modern-furniture/)");

            addonRepository.save(modernFurniture);

            Archivo modernFile = new Archivo();
            modernFile.setNombreMostrado("Modern Furniture Add-On");
            modernFile.setUrl("3dc8c67e-5ea0-4e23-a099-4f94974266b5_PHYSICS_GLUE_v0.3.mcaddon");
            modernFile.setVersionJuego("1.20+");
            modernFile.setVersionAddon("v0.3");
            modernFile.setTipo("mcaddon");
            modernFile.setDisponible(true);
            modernFile.setAddon(modernFurniture);
            archivoRepository.save(modernFile);

            Archivo modernFile2 = new Archivo();
            modernFile2.setNombreMostrado("Modern Lite");
            modernFile2.setUrl("3dc8c67e-5ea0-4e23-a099-4f94974266b5_PHYSICS_GLUE_v0.3.mcaddon");
            modernFile2.setVersionJuego("1.20+");
            modernFile2.setVersionAddon("v0.12");
            modernFile2.setTipo("mcaddon");
            modernFile2.setDisponible(true);
            modernFile2.setAddon(modernFurniture);
            archivoRepository.save(modernFile2);


            addonRepository.insertarCreadorAddon(adminUsuario.getId(), modernFurniture.getId(), "aceptado");


            Addon feudalFurniture = new Addon();
            feudalFurniture.setNombre("Feudal Furniture");
            feudalFurniture.setTipo("add-on");
            feudalFurniture.setTag("Decoracion");
            feudalFurniture.setUrlMiniatura("https://www.trmc-addons.com/add-ons/feudal-furniture/thumbnail.webp");
            feudalFurniture.setDescripcion("Feudal Furniture is a medieval furniture addon with over 800 pieces of furniture and decorations in a simple, vanilla-style format. The addon is designed to seamlessly fit into a Minecraft-like environment, allowing you to decorate any type of area, such as villages, bases, mines, castles, and more.");
            feudalFurniture.setTextoAddon("Feudal Furniture is a medieval furniture addon with over **800 pieces** of furniture and decorations in a simple, vanilla-style format. The addon is designed to seamlessly fit into a Minecraft-like environment, allowing you to decorate any type of area, such as villages, bases, mines, castles, and more. \n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "[![](https://www.trmc-addons.com/assets/download.png)](https://www.trmc-addons.com/add-ons/feudal-furniture/)\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "🪓 **Spikes:** Functional for creating traps for players and mobs.\n" +
                    "\n" +
                    "🕯️ **Medieval Lamps:** Functional lamps.\n" +
                    "\n" +
                    "🧺 **Baskets:** Can hold items such as apples or carrots.\n" +
                    "\n" +
                    "🚪 **Gates:** Can be opened and closed manually.\n" +
                    "\n" +
                    "⚙️ **Automatic Trapdoors:** Useful for traps; if a player stands on them, the trapdoor will automatically open.\n" +
                    "\n" +
                    "🔥 **Fireplace:** Can be lit with a flint and steel, and if a chimney is placed above, smoke will come out.\n" +
                    "\n" +
                    "🪑 **Chairs and Benches:** Can be used to sit.\n" +
                    "\n" +
                    "🏚️ **Windows:** Can be opened and closed manually.\n" +
                    "\n" +
                    "📜 **Signs:** Directional arrows for pathfinding and signs to mark inns, mines, markets, etc.\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "The uses of this add-on in a **server or multiplayer** world are highly compatible with roleplay, allowing you to create functional markets, castles with trap systems, or abandoned spaces such as ruins or deserted castles.\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "The categories of the boxes in this Add-On are based on their wood type. These are as follows:\n" +
                    "\n" +
                    "*   Oak Wood Box\n" +
                    "*   Spruce Wood Box\n" +
                    "*   Birch Wood Box\n" +
                    "*   Jungle Wood Box\n" +
                    "*   Dark Oak Wood Box\n" +
                    "*   Cherry Wood Box\n" +
                    "*   Crimson Wood Box\n" +
                    "*   Mangrove Wood Box\n" +
                    "*   Acacia Wood Box\n" +
                    "*   Bamboo Wood Box\n" +
                    "*   Warped Wood Box\n" +
                    "*   Pale Wood Box\n" +
                    "\n" +
                    "There is one last box, the **Miscellaneous Box** or Decorative Box. This box contains all decorations that are not primarily wood-based, including types of bricks, torch holders, trapdoors, and more. This box will look similar to the Oak Wood Box but in a smaller version.\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "There is a wide **variety of functionalities** for the furniture, such as sitting on chairs, opening windows, using trapdoors, lighting fireplaces, and more. If you want to know if an item is functional before choosing it, a message with a hand icon labeled \"Functionality\" will appear next to its name. This indicates that you can interact with it.\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "![](https://www.trmc-addons.com/assets/mcpedl_img/fea2.webp)\n" +
                    "\n" +
                    "**Creative:**\n" +
                    "\n" +
                    "To get the furniture in Creative mode, you will find it in the decoration tab of the Creative menu. If you want to obtain it using commands, you will need to type /give @p ff: (then you can add the name of the furniture you want).\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "**Survival:**\n" +
                    "\n" +
                    "To create the medieval furniture, you will need clay and a stonecutter. Then, you will need to place the clay in the stonecutter. A list of boxes will appear this list represents the furniture categories (Wooden boxes of Oak, Spruce, Birch, Jungle, Dark Oak, Cherry, Crimson, Mangrove, Acacia, and Bamboo will appear).\n" +
                    "\n" +
                    "You will have to choose a box. Once selected, you will place it back in the stonecutter, and it will display all the available options. From there, it's up to you to decorate!\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "![](https://media.forgecdn.net/attachments/description/null/description_9180bcbb-c960-4e8f-951e-9a82f2c647fe.png)\n" +
                    "\n" +
                    "![](https://media.forgecdn.net/attachments/description/null/description_ff062505-9b50-47c7-98cd-28e5fa67afd7.png)\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "![](https://www.trmc-addons.com/assets/mcpedl_img/fea3.webp)\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "# ![](https://www.trmc-addons.com/assets/mcpedl_img/001.webp)\n" +
                    "\n" +
                    "![](https://www.trmc-addons.com/assets/mcpedl_img/002.webp)\n" +
                    "\n" +
                    "# ![](https://www.trmc-addons.com/add-ons/feudal-furniture/images/2.webp)\n" +
                    "\n" +
                    "![](https://www.trmc-addons.com/add-ons/feudal-furniture/images/3.webp)\n" +
                    "\n" +
                    "! ![](https://www.trmc-addons.com/add-ons/feudal-furniture/images/4.webp)\n" +
                    "\n" +
                    "! ![](https://www.trmc-addons.com/add-ons/feudal-furniture/images/5.webp)\n" +
                    "\n" +
                    "! ![](https://www.trmc-addons.com/add-ons/feudal-furniture/images/6.webp)\n" +
                    "\n" +
                    "&nbsp;\n" +
                    "\n" +
                    "[![](https://www.trmc-addons.com/assets/mcpedl_img/patreon1.webp)](https://www.patreon.com/cw/trotamundos872)\n" +
                    "\n" +
                    "<p style=\"text-align:center\">https://www.patreon.com/cw/trotamundos872</p>\n" +
                    "\n" +
                    "[![](https://www.trmc-addons.com/assets/mcpedl_img/patreon2.webp)](https://www.patreon.com/cw/trotamundos872)\n" +
                    "\n" +
                    "[![](https://www.trmc-addons.com/assets/mcpedl_img/patreon_button.webp)](https://www.patreon.com/cw/trotamundos872)\n" +
                    "\n" +
                    "[![](https://www.trmc-addons.com/assets/mcpedl_img/discord1.webp)](https://discord.gg/dKfv3SHpxq)\n" +
                    "\n" +
                    "<p style=\"text-align:center\">[![](https://www.trmc-addons.com/assets/mcpedl_img/discord_button.webp)](https://discord.gg/dKfv3SHpxq)</p>&nbsp;\n" +
                    "\n" +
                    "# Downloads | Official Site\n" +
                    "\n" +
                    "*   https://www.trmc-addons.com/add-ons/feudal-furniture/");

            addonRepository.save(feudalFurniture);


            Archivo feudalFile = new Archivo();
            feudalFile.setNombreMostrado("Feudal Furniture Add-On");
            feudalFile.setUrl("3dc8c67e-5ea0-4e23-a099-4f94974266b5_PHYSICS_GLUE_v0.3.mcaddon");
            feudalFile.setVersionJuego("1.20+");
            feudalFile.setVersionAddon("v0.3");
            feudalFile.setTipo("mcaddon");
            feudalFile.setDisponible(true);
            feudalFile.setAddon(feudalFurniture);
            archivoRepository.save(feudalFile);

            addonRepository.insertarCreadorAddon(adminUsuario.getId(), feudalFurniture.getId(), "aceptado");



            
            Addon customPlayerHeads = new Addon();
            customPlayerHeads.setNombre("Custom Player Heads");
            customPlayerHeads.setTipo("mapa");
            customPlayerHeads.setTag("Decoracion");
            customPlayerHeads.setUrlMiniatura("https://www.trmc-addons.com/add-ons/custom-player-heads/thumbnail.webp");
            customPlayerHeads.setDescripcion("Placing player heads in Minecraft is essential, and currently there is no native function in Minecraft Bedrock to do so. This tool solves that! Custom Player Heads is an online program for generating player heads!");
            customPlayerHeads.setTextoAddon("Placing player heads in Minecraft is essential, and currently there is no native function in Minecraft Bedrock to do so. This tool solves that! Custom Player Heads is an online program for generating player heads!\r\n" + //
                                "\r\n" + //
                                "[![alt text](https://www.trmc-addons.com/add-ons/custom-player-heads/images/title.webp)](https://www.trmc-addons.com/cph)\r\n" + //
                                "\r\n" + //
                                "<h2 style=\"text-align:center\">[![](https://www.trmc-addons.com/add-ons/custom-paintings/images/customize.webp)](https://www.trmc-addons.com/cph)</h2><h2 style=\"text-align:center\">Main Features:</h2>\r\n" + //
                                "\r\n" + //
                                "**Player Heads in Minecraft:** Player heads are a very popular item in the Java Edition of Minecraft. They are widely used for roleplay, trophies, decoration, and many creative purposes.\r\n" + //
                                "\r\n" + //
                                "**Decoration Options:** Custom head skins allow you to simulate many objects such as portals, mini blocks, books, food, and much more, making them perfect for detailed builds and creative designs.\r\n" + //
                                "\r\n" + //
                                "**Put it on your head:** Imported heads can be equipped on your head, working exactly like a vanilla item.\r\n" + //
                                "\r\n" + //
                                "You can preview how the head block will look below everything in the online editor.\r\n" + //
                                "\r\n" + //
                                "## Images\r\n" + //
                                "\r\n" + //
                                "![](https://www.trmc-addons.com/add-ons/custom-player-heads/images/2.webp)\r\n" + //
                                "\r\n" + //
                                "![](https://www.trmc-addons.com/add-ons/custom-player-heads/images/1.webp)\r\n" + //
                                "\r\n" + //
                                "![](https://www.trmc-addons.com/add-ons/custom-player-heads/images/3.webp)\r\n" + //
                                "\r\n" + //
                                "## Crafting\r\n" + //
                                "\r\n" + //
                                "All generated heads will only be available if you place a soul sand block in the stonecutter.\r\n" + //
                                "\r\n" + //
                                " \r\n" + //
                                "\r\n" + //
                                "## 🔴  IMPORTANT | TUTORIAL: Using the Editor  🔴\r\n" + //
                                "\r\n" + //
                                " \r\n" + //
                                "\r\n" + //
                                "To get your add-on, follow these steps in our web editor: [https://www.trmc-addons.com/cph](https://www.trmc-addons.com/cph)\r\n" + //
                                "\r\n" + //
                                "1.  Upload Skins: Upload the Minecraft head skins you need.\r\n" + //
                                "    \r\n" + //
                                "2.  Add Heads: Once all the head skins are uploaded, make sure everything is ready.\r\n" + //
                                "    \r\n" + //
                                "3.  Export: Click \"Export Addon\" to generate your file.\r\n" + //
                                "    \r\n" + //
                                "4.  Download & Use: A .mcaddon file will be downloaded. Run it to install and use your custom player heads in Minecraft.\r\n" + //
                                "    \r\n" + //
                                "\r\n" + //
                                "![](https://www.trmc-addons.com/add-ons/custom-player-heads/images/Video Project 2.gif)\r\n" + //
                                "\r\n" + //
                                "# Downloads | Official Site\r\n" + //
                                "\r\n" + //
                                "*   [Download Custom Player Heads](https://www.trmc-addons.com/cph)\r\n" + //
                                "\r\n" + //
                                "undefined");

            addonRepository.save(customPlayerHeads);

            Archivo customPlayerHeadsFile = new Archivo();
            customPlayerHeadsFile.setNombreMostrado("Custom Player Add-On");
            customPlayerHeadsFile.setUrl("3dc8c67e-5ea0-4e23-a099-4f94974266b5_PHYSICS_GLUE_v0.3.mcaddon");
            customPlayerHeadsFile.setVersionJuego("1.20+");
            customPlayerHeadsFile.setVersionAddon("v0.1");
            customPlayerHeadsFile.setTipo("mcaddon");
            customPlayerHeadsFile.setDisponible(true);
            customPlayerHeadsFile.setAddon(customPlayerHeads);
            archivoRepository.save(customPlayerHeadsFile);

            addonRepository.insertarCreadorAddon(adminUsuario.getId(), customPlayerHeads.getId(), "aceptado");






            
            
            Addon sleepingBags = new Addon();
            sleepingBags.setNombre("Sleeping Bags");
            sleepingBags.setTipo("skin");
            sleepingBags.setTag("Decoracion");
            sleepingBags.setUrlMiniatura("https://www.trmc-addons.com/add-ons/sleeping-bags/thumbnail.webp");
            sleepingBags.setDescripcion("Sleeping Bags is an add-on that adds functional sleeping bags for Minecraft Bedrock.");
            sleepingBags.setTextoAddon("Placing player heads in Minecraft is essential, and currently there is no native function in Minecraft Bedrock to do so. This tool solves that! Custom Player Heads is an online program for generating player heads!\r\n" + //
                                "\r\n" + //
                                "[![alt text](https://www.trmc-addons.com/add-ons/custom-player-heads/images/title.webp)](https://www.trmc-addons.com/cph)\r\n" + //
                                "\r\n" + //
                                "<h2 style=\"text-align:center\">[![](https://www.trmc-addons.com/add-ons/custom-paintings/images/customize.webp)](https://www.trmc-addons.com/cph)</h2><h2 style=\"text-align:center\">Main Features:</h2>\r\n" + //
                                "\r\n" + //
                                "**Player Heads in Minecraft:** Player heads are a very popular item in the Java Edition of Minecraft. They are widely used for roleplay, trophies, decoration, and many creative purposes.\r\n" + //
                                "\r\n" + //
                                "**Decoration Options:** Custom head skins allow you to simulate many objects such as portals, mini blocks, books, food, and much more, making them perfect for detailed builds and creative designs.\r\n" + //
                                "\r\n" + //
                                "**Put it on your head:** Imported heads can be equipped on your head, working exactly like a vanilla item.\r\n" + //
                                "\r\n" + //
                                "You can preview how the head block will look below everything in the online editor.\r\n" + //
                                "\r\n" + //
                                "## Images\r\n" + //
                                "\r\n" + //
                                "![](https://www.trmc-addons.com/add-ons/custom-player-heads/images/2.webp)\r\n" + //
                                "\r\n" + //
                                "![](https://www.trmc-addons.com/add-ons/custom-player-heads/images/1.webp)\r\n" + //
                                "\r\n" + //
                                "![](https://www.trmc-addons.com/add-ons/custom-player-heads/images/3.webp)\r\n" + //
                                "\r\n" + //
                                "## Crafting\r\n" + //
                                "\r\n" + //
                                "All generated heads will only be available if you place a soul sand block in the stonecutter.\r\n" + //
                                "\r\n" + //
                                " \r\n" + //
                                "\r\n" + //
                                "## 🔴  IMPORTANT | TUTORIAL: Using the Editor  🔴\r\n" + //
                                "\r\n" + //
                                " \r\n" + //
                                "\r\n" + //
                                "To get your add-on, follow these steps in our web editor: [https://www.trmc-addons.com/cph](https://www.trmc-addons.com/cph)\r\n" + //
                                "\r\n" + //
                                "1.  Upload Skins: Upload the Minecraft head skins you need.\r\n" + //
                                "    \r\n" + //
                                "2.  Add Heads: Once all the head skins are uploaded, make sure everything is ready.\r\n" + //
                                "    \r\n" + //
                                "3.  Export: Click \"Export Addon\" to generate your file.\r\n" + //
                                "    \r\n" + //
                                "4.  Download & Use: A .mcaddon file will be downloaded. Run it to install and use your custom player heads in Minecraft.\r\n" + //
                                "    \r\n" + //
                                "\r\n" + //
                                "![](https://www.trmc-addons.com/add-ons/custom-player-heads/images/Video Project 2.gif)\r\n" + //
                                "\r\n" + //
                                "# Downloads | Official Site\r\n" + //
                                "\r\n" + //
                                "*   [Download Custom Player Heads](https://www.trmc-addons.com/cph)\r\n" + //
                                "\r\n" + //
                                "undefined");

            addonRepository.save(sleepingBags);

            Archivo sleepingBagsFile = new Archivo();
            sleepingBagsFile.setNombreMostrado("Sleeping Bags");
            sleepingBagsFile.setUrl("3dc8c67e-5ea0-4e23-a099-4f94974266b5_PHYSICS_GLUE_v0.3.mcaddon");
            sleepingBagsFile.setVersionJuego("1.18+");
            sleepingBagsFile.setVersionAddon("v0.21");
            sleepingBagsFile.setTipo("mcaddon");
            sleepingBagsFile.setDisponible(true);
            sleepingBagsFile.setAddon(sleepingBags);
            archivoRepository.save(sleepingBagsFile);

            addonRepository.insertarCreadorAddon(adminUsuario.getId(), sleepingBags.getId(), "aceptado");

            Addon medievalStructures = new Addon();
            medievalStructures.setNombre("Medieval Structures");
            medievalStructures.setTipo("mapas");
            medievalStructures.setTag("pvp");
            medievalStructures.setUrlMiniatura("https://www.trmc-addons.com/add-ons/medieval-structures/thumbnail.webp");
            medievalStructures.setDescripcion("Medieval Structures is a Minecraft Bedrock add-on that adds dozens of new medieval structures to your world, powered by our own Feudal Furniture add-on!");
            medievalStructures.setTextoAddon("Placing player heads in Minecraft is essential, and currently there is no native function in Minecraft Bedrock to do so. This tool solves that! Custom Player Heads is an online program for generating player heads!\r\n" + //
                                "\r\n" + //
                                "[![alt text](https://www.trmc-addons.com/add-ons/custom-player-heads/images/title.webp)](https://www.trmc-addons.com/cph)\r\n" + //
                                "\r\n" + //
                                "<h2 style=\"text-align:center\">[![](https://www.trmc-addons.com/add-ons/custom-paintings/images/customize.webp)](https://www.trmc-addons.com/cph)</h2><h2 style=\"text-align:center\">Main Features:</h2>\r\n" + //
                                "\r\n" + //
                                "**Player Heads in Minecraft:** Player heads are a very popular item in the Java Edition of Minecraft. They are widely used for roleplay, trophies, decoration, and many creative purposes.\r\n" + //
                                "\r\n" + //
                                "**Decoration Options:** Custom head skins allow you to simulate many objects such as portals, mini blocks, books, food, and much more, making them perfect for detailed builds and creative designs.\r\n" + //
                                "\r\n" + //
                                "**Put it on your head:** Imported heads can be equipped on your head, working exactly like a vanilla item.\r\n" + //
                                "\r\n" + //
                                "You can preview how the head block will look below everything in the online editor.\r\n" + //
                                "\r\n" + //
                                "## Images\r\n" + //
                                "\r\n" + //
                                "![](https://www.trmc-addons.com/add-ons/custom-player-heads/images/2.webp)\r\n" + //
                                "\r\n" + //
                                "![](https://www.trmc-addons.com/add-ons/custom-player-heads/images/1.webp)\r\n" + //
                                "\r\n" + //
                                "![](https://www.trmc-addons.com/add-ons/custom-player-heads/images/3.webp)\r\n" + //
                                "\r\n" + //
                                "## Crafting\r\n" + //
                                "\r\n" + //
                                "All generated heads will only be available if you place a soul sand block in the stonecutter.\r\n" + //
                                "\r\n" + //
                                " \r\n" + //
                                "\r\n" + //
                                "## 🔴  IMPORTANT | TUTORIAL: Using the Editor  🔴\r\n" + //
                                "\r\n" + //
                                " \r\n" + //
                                "\r\n" + //
                                "To get your add-on, follow these steps in our web editor: [https://www.trmc-addons.com/cph](https://www.trmc-addons.com/cph)\r\n" + //
                                "\r\n" + //
                                "1.  Upload Skins: Upload the Minecraft head skins you need.\r\n" + //
                                "    \r\n" + //
                                "2.  Add Heads: Once all the head skins are uploaded, make sure everything is ready.\r\n" + //
                                "    \r\n" + //
                                "3.  Export: Click \"Export Addon\" to generate your file.\r\n" + //
                                "    \r\n" + //
                                "4.  Download & Use: A .mcaddon file will be downloaded. Run it to install and use your custom player heads in Minecraft.\r\n" + //
                                "    \r\n" + //
                                "\r\n" + //
                                "![](https://www.trmc-addons.com/add-ons/custom-player-heads/images/Video Project 2.gif)\r\n" + //
                                "\r\n" + //
                                "# Downloads | Official Site\r\n" + //
                                "\r\n" + //
                                "*   [Download Custom Player Heads](https://www.trmc-addons.com/cph)\r\n" + //
                                "\r\n" + //
                                "undefined");

            addonRepository.save(medievalStructures);

            Archivo medievalStructuresFile = new Archivo();
            medievalStructuresFile.setNombreMostrado("Medieval Structures Plus");
            medievalStructuresFile.setUrl("3dc8c67e-5ea0-4e23-a099-4f94974266b5_PHYSICS_GLUE_v0.3.mcaddon");
            medievalStructuresFile.setVersionJuego("1.14+");
            medievalStructuresFile.setVersionAddon("v0.21");
            medievalStructuresFile.setTipo("mcaddon");
            medievalStructuresFile.setDisponible(true);
            medievalStructuresFile.setAddon(medievalStructures);
            archivoRepository.save(medievalStructuresFile);

            addonRepository.insertarCreadorAddon(adminUsuario.getId(), medievalStructures.getId(), "aceptado");


        }
    }
}
