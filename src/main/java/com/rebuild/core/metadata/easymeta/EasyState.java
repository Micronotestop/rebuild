/*
Copyright (c) REBUILD <https://getrebuild.com/> and/or its owners. All rights reserved.

rebuild is dual-licensed under commercial and open source licenses (GPLv3).
See LICENSE and COMMERCIAL in the project root for license information.
*/

package com.rebuild.core.metadata.easymeta;

import cn.devezhao.persist4j.Field;
import com.rebuild.core.metadata.impl.FieldExtConfigProps;
import com.rebuild.core.support.i18n.Language;
import com.rebuild.core.support.state.StateHelper;

/**
 * @author devezhao
 * @since 2020/11/17
 */
public class EasyState extends EasyField {
    private static final long serialVersionUID = -5160207555364899330L;

    protected EasyState(Field field, DisplayType displayType) {
        super(field, displayType);
    }

    @Override
    public Object convertCompatibleValue(Object value, EasyField targetField) {
        DisplayType targetType = targetField.getDisplayType();
        boolean is2Text = targetType == DisplayType.TEXT || targetType == DisplayType.NTEXT;
        if (is2Text) {
            return wrapValue(value);
        }

        Integer intValue = (Integer) value;
        return intValue;
    }

    @Override
    public Object wrapValue(Object value) {
        String stateClass = getExtraAttr(FieldExtConfigProps.STATE_STATECLASS);
        return Language.L(StateHelper.valueOf(stateClass, (Integer) value));
    }
}
