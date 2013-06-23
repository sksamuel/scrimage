package thirdparty.marvin.image;

import java.util.LinkedHashMap;

/**
 * This class stores and manages plug-ins attributes and either
 * integrates them with Marvin features, such as FilterHistory,
 * Statistics and MarvinPluginWindow.
 *
 * @version 02/13/08
 */
public class MarvinAttributes {
    private MarvinPlugin plugin;
    protected LinkedHashMap<String, Object> hashAttributes;

    /**
     * Constructor
     */
    public MarvinAttributes(MarvinPlugin p) {
        plugin = p;
        hashAttributes = new LinkedHashMap<String, Object>();
    }

    public MarvinAttributes() {
        this(null);
    }

    /**
     * Set an attribute.
     *
     * @param name  attribute name.
     * @param value attribute value.
     */
    public void set(String name, Object value) {
        hashAttributes.put(name, value);

        if (plugin != null) {
            plugin.invalidate();
        }
    }

    /**
     * Set a list of parameters. Format: (String)name, (Object)value...
     *
     * @param params
     */
    public void set(Object... params) {
        for (int i = 0; i < params.length; i += 2) {
            hashAttributes.put((String) params[i], params[i + 1]);
        }

        if (plugin != null) {
            plugin.invalidate();
        }
    }

    /**
     * Get an attribute by its name.
     *
     * @param name attribute�s name.
     * @return the specified attribute as an Object.
     */
    public Object get(String name) {
        return hashAttributes.get(name);
    }

    public Object get(String name, Object defaultValue) {
        Object o = get(name);
        if (o != null) {
            return o;
        }
        return defaultValue;
    }

    /**
     * Returns all attributes� name and value as a String array.
     *
     * @return string array with all attributes� name and value.
     */
    public String[] toStringArray() {
        String key;
        String attrs[] = new String[hashAttributes.size() * 2];
        String[] keys = hashAttributes.keySet().toArray(new String[0]);
        for (int x = 0; x < keys.length; x++) {
            attrs[(x * 2)] = keys[x];
            attrs[(x * 2) + 1] = "" + hashAttributes.get(keys[x]);
        }
        return attrs;
    }

    /**
     * returns an array containing the attrbiute values
     */
    public Object[] getValues() {
        Object o[] = hashAttributes.entrySet().toArray(new Object[0]);
        return o;
    }

    /**
     * Clones a MarvinAttributes Object.
     */
    public MarvinAttributes clone() {
        MarvinAttributes attrs = new MarvinAttributes();
        attrs.plugin = plugin;
        String key;
        String[] keys = hashAttributes.keySet().toArray(new String[0]);
        for (int x = 0; x < keys.length; x++) {
            attrs.set(keys[x], hashAttributes.get(keys[x]));
        }
        return attrs;
    }
}