package hk.polyu.comp;

import java.util.HashMap;

public class HprofFrame {

    static HashMap<Long, HprofFrame> frameMap = new HashMap<Long, HprofFrame>();

    long stackFrameId;
    long methodNameStringId;
    long methodSigStringId;
    long sourceFileNameStringId;
    int classSerialNum;
    int location;

    public static HprofFrame construct(long stackFrameId, long methodNameStringId, long methodSigStringId, long sourceFileNameStringId, int classSerialNum, int location) {
        HprofFrame constructedFrame = new HprofFrame(stackFrameId, methodNameStringId, methodSigStringId, sourceFileNameStringId, classSerialNum, location);
        frameMap.put(stackFrameId, constructedFrame);
        return constructedFrame;
    }

    public HprofFrame(long stackFrameId, long methodNameStringId, long methodSigStringId, long sourceFileNameStringId, int classSerialNum, int location) {
        this.stackFrameId = stackFrameId;
        this.methodNameStringId = methodNameStringId;
        this.methodSigStringId = methodSigStringId;
        this.sourceFileNameStringId = sourceFileNameStringId;
        this.classSerialNum = classSerialNum;
        this.location = location;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LoadedClass.getClassName(classSerialNum) + "#" + DumpHandler.idStringMap.get(methodNameStringId));
        switch (location) {

            case 0:
                sb.append("no line information available");
                break;

            case -1:
                sb.append("unknown location");
                break;

            case -2:
                sb.append("compiled method");
                break;

            case -3:
                sb.append("native method");
                break;

            default:
                sb.append("(" + location + ")");
                break;

        }
        return sb.toString();
    }
}
