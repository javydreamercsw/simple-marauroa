package simple.common;

import simple.server.core.action.WellKnownActionConstant;

/**
 * Constants using during the procession of an Equipment action.
 */
public interface EquipActionConsts {

    String BASE_ITEM = "baseitem";
    String BASE_SLOT = "baseslot";
    String BASE_OBJECT = "baseobject";
    String TYPE = WellKnownActionConstant.TYPE;
    String TARGET_OBJECT = "targetobject";
    String TARGET_SLOT = "targetslot";
    String GROUND_X = "x";
    String GROUND_Y = "y";
    String QUANTITY = "quantity";
    double MAXDISTANCE = 0.25;
    int MAX_CONTAINED_DEPTH = 25;
}
