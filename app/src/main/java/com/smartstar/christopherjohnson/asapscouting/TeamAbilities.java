package com.smartstar.christopherjohnson.asapscouting;

import android.widget.CheckBox;

import java.util.Collection;
import java.util.Map;

/**
 * Created by christopher.johnson on 11/24/16.
 *
 * This class represents a {@link Team team's} scoring abilities.
 * There is an experimental procedural data mapper/analyzer.
 */

public class TeamAbilities {
    // This stores the String name of the ability and the Boolean value of the ability
    private Map<String, Boolean> abilityMapperData = null;

    public TeamAbilities(CheckBox boxes[]) {
        for (CheckBox box : boxes) {
            this.abilityMapperData.put(box.getText().toString(), box.isChecked());
        }
    }

    public TeamAbilities(Map<String, Boolean> data) {
        this.abilityMapperData = data;
    }

    public Boolean getAbilityValue(String key) {
        if (abilityMapperData.containsKey(key)) {
            return abilityMapperData.get(key);
        } else {
            return null;
        }
    }

    public int getNumAbilities() {
        return abilityMapperData.size();
    }

    public Map<String, Boolean> getAbilityMapperData() {
        return abilityMapperData;
    }
}
