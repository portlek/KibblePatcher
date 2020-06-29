package fr.kibblesland.patcher;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

public class InventoryCompact implements Opcodes {
    private static final String INVENTORY = "org/bukkit/inventory/Inventory.class";
    private static final String CUSTOM_INVENTORY = "org/bukkit/craftbukkit/$NMS/inventory/CraftInventoryCustom.class";

    public static void check(Map<String, byte[]> map, String mth,final int[] stats) {
        String NMS = mth.substring(21, mth.lastIndexOf('/'));
        byte[] inv = map.get(INVENTORY);
        ClassReader classReader = new ClassReader(inv);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        for (MethodNode methods: classNode.methods) {
            if (methods.name.equals("getTitle")) {
                return; // API already exists in this Spigot (Do nothing)
            }
        }
        if (classNode.version < V1_8) { // For default in interface
            classNode.version = V1_8;
        }
        MethodNode tmp;
        classNode.methods.add(tmp = new MethodNode(ACC_PUBLIC|ACC_SYNTHETIC, "getName", "()Ljava/lang/String;", null, null));
        LabelNode labelNode = new LabelNode();
        tmp.instructions = new InsnList();
        tmp.instructions.add(labelNode);
        tmp.instructions.add(new LineNumberNode(12345, labelNode));
        tmp.instructions.add(new VarInsnNode(ALOAD, 0));
        tmp.instructions.add(new MethodInsnNode(INVOKEINTERFACE, "org/bukkit/inventory/Inventory", "getTitle", "()Ljava/lang/String;", true));
        tmp.instructions.add(new InsnNode(ARETURN));
        classNode.methods.add(tmp = new MethodNode(ACC_PUBLIC|ACC_SYNTHETIC, "getTitle", "()Ljava/lang/String;", null, null));
        labelNode = new LabelNode();
        tmp.instructions = new InsnList();
        tmp.instructions.add(labelNode);
        tmp.instructions.add(new LineNumberNode(12345, labelNode));
        tmp.instructions.add(new VarInsnNode(ALOAD, 0));
        tmp.instructions.add(new MethodInsnNode(INVOKEINTERFACE, "org/bukkit/inventory/Inventory", "getType", "()Lorg/bukkit/event/inventory/InventoryType;", true));
        tmp.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "org/bukkit/event/inventory/InventoryType", "getDefaultTitle", "()Ljava/lang/String;", false));
        tmp.instructions.add(new InsnNode(ARETURN));
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        map.put(INVENTORY, classWriter.toByteArray());
        classReader = new ClassReader(map.get(CUSTOM_INVENTORY.replace("$NMS", NMS)));
        classNode = new ClassNode();
        classReader.accept(classNode, 0);
        classNode.methods.add(tmp = new MethodNode(ACC_PUBLIC|ACC_SYNTHETIC, "getTitle", "()Ljava/lang/String;", null, null));
        labelNode = new LabelNode();
        tmp.instructions = new InsnList();
        tmp.instructions.add(labelNode);
        tmp.instructions.add(new LineNumberNode(12345, labelNode));
        tmp.instructions.add(new VarInsnNode(ALOAD, 0));
        tmp.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "org/bukkit/craftbukkit/"+NMS+"/inventory/CraftInventoryCustom", "getInventory", "()Lnet/minecraft/server/"+NMS+"/IInventory;", false));
        tmp.instructions.add(new TypeInsnNode(CHECKCAST, "org/bukkit/craftbukkit/"+NMS+"/inventory/CraftInventoryCustom$MinecraftInventory"));
        tmp.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "org/bukkit/craftbukkit/"+NMS+"/inventory/CraftInventoryCustom$MinecraftInventory", "getTitle", "()Ljava/lang/String;", false));
        tmp.instructions.add(new InsnNode(ARETURN));
        classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        map.put(CUSTOM_INVENTORY.replace("$NMS", NMS), classWriter.toByteArray());
        stats[3]++;
    }
}
