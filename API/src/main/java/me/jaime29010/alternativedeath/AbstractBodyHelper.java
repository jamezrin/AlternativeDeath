package me.jaime29010.alternativedeath;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public abstract class AbstractBodyHelper {
    public abstract void sendBody(Player player);

    public abstract void sendBody(Player player, Location location);

    public abstract Object cloneDataWatcher(Player player, int entityId);

    public final void setValue(Object object, String field, Object value) {
        try {
            Class c1 = object.getClass();
            Field f1 = c1.getDeclaredField(field);
            f1.setAccessible(true);
            f1.set(object, value);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
