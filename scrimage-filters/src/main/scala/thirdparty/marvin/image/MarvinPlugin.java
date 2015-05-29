/**
 Marvin Project <2007-2009>

 Initial version by:

 Danilo Rosetto Munoz
 Fabio Andrijauskas
 Gabriel Ambrosio Archanjo

 site: http://marvinproject.sourceforge.net

 GPL
 Copyright (C) <2007>

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package thirdparty.marvin.image;

/**
 * Generic Marvin Plug-in. All application plug-ins must be implement this interface. Currently,
 * it�s empty because Marvin suports only one type of plug-in. Nevertheless, different types of
 * plug-ins are expected.
 *
 * @version 1.0 02/13/08
 */
public interface MarvinPlugin {

    /**
     * Ensures that this plug-in is working consistently to its attributes.
     */
    public void validate();

    /**
     * Invalidate this plug-in. It means that the attributes were changed and the plug-in needs to check whether
     * or not change its behavior.
     */
    public void invalidate();

    /**
     * Determines whether this plug-in is valid. A plug-in is valid when it is correctly configured given a set
     * of attributes. When an attribute is changed, the plug-in becomes invalid until the method validate() is
     * called.
     *
     * @return
     */
    public boolean isValid();

    /**
     * @return MarvinAttributes object associated with this plug-in
     */
    public MarvinAttributes getAttributes();

    /**
     * Set an attribute
     *
     * @param attrName attribute name
     * @param value    attribute value
     */
    public void setAttribute(String attrName, Object value);

    /**
     * Set a list of attributes. Format: (String)name, (Object)value...
     */
    public void setAttributes(Object... params);

    /**
     * @param attrName attribute name
     * @return the attribute�s value
     */
    public Object getAttribute(String attrName);

}
