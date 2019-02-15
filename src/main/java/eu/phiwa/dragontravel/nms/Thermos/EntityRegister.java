package eu.phiwa.dragontravel.nms.Thermos;

import eu.phiwa.dragontravel.core.hooks.server.IEntityRegister;
import net.minecraft.server.v1_7_R4.EntityTypes;

import java.lang.reflect.Field;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class EntityRegister implements IEntityRegister {

    @Override
    public boolean registerEntity() {
        try {
            Class<EntityTypes> entityTypeClass = EntityTypes.class;

            Field c = entityTypeClass.getDeclaredField("field_75625_b");
            c.setAccessible(true);
            HashMap<String, Class<?>> c_map = (HashMap<String, Class<?>>) c.get(null);
            c_map.put("RyeDragon", RyeDragon.class);

            Field d = entityTypeClass.getDeclaredField("field_75626_c");
            d.setAccessible(true);
            HashMap<Class<?>, String> d_map = (HashMap<Class<?>, String>) d.get(null);
            d_map.put(RyeDragon.class, "RyeDragon");

            Field e = entityTypeClass.getDeclaredField("field_75623_d");
            e.setAccessible(true);
            HashMap<Integer, Class<?>> e_map = (HashMap<Integer, Class<?>>) e.get(null);
            e_map.put(63, RyeDragon.class);

            Field f = entityTypeClass.getDeclaredField("field_75624_e");
            f.setAccessible(true);
            HashMap<Class<?>, Integer> f_map = (HashMap<Class<?>, Integer>) f.get(null);
            f_map.put(RyeDragon.class, 63);

            Field g = entityTypeClass.getDeclaredField("field_75622_f");
            g.setAccessible(true);
            HashMap<String, Integer> g_map = (HashMap<String, Integer>) g.get(null);
            g_map.put("RyeDragon", 63);

            return true;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }
}
