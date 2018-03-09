package hk.polyu.comp;

import java.util.HashMap;
import java.util.Map;

public class LoadedClass {

    static Map<Integer, LoadedClass> serialNumMap = new HashMap<>();
    static Map<Long, LoadedClass> objIdMap = new HashMap<>();
    int classSerialNum;
    long classObjId;
    int stackTraceSerialNum;
    long classNameStringId;


    public static LoadedClass construct(int classSerialNum, long classObjId, int stackTraceSerialNum, long classNameStringId) {
        LoadedClass constructed = new LoadedClass(classSerialNum, classObjId, stackTraceSerialNum, classNameStringId);
        serialNumMap.put(classSerialNum, constructed);
        objIdMap.put(classObjId, constructed);
        return constructed;
    }

    public static String getClassName(int classSerialNum) {
        return MyHandler.idStringMap.get(serialNumMap.get(classSerialNum).classNameStringId);
    }

    public static String getClassName(long classObjId) {
        return MyHandler.idStringMap.get(objIdMap.get(classObjId).classNameStringId);
    }

    private LoadedClass(int classSerialNum, long classObjId, int stackTraceSerialNum, long classNameStringId) {
        this.classSerialNum = classSerialNum;
        this.classObjId = classObjId;
        this.stackTraceSerialNum = stackTraceSerialNum;
        this.classNameStringId = classNameStringId;
    }


}
