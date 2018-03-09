package hk.polyu.comp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HprofTrace {
    static HashMap<Integer, HprofTrace> traceMap = new HashMap<Integer, HprofTrace>();

    int stackTraceSerialNum;
    int threadSerialNum;
    int numFrames;
    long[] stackFrameIds;

    public static HprofTrace construct(int stackTraceSerialNum, int threadSerialNum, int numFrames, long[] stackFrameIds) {
        HprofTrace constructedTrace = new HprofTrace(stackTraceSerialNum, threadSerialNum, numFrames, stackFrameIds);
        traceMap.put(stackTraceSerialNum, constructedTrace);
        return constructedTrace;
    }

    private HprofTrace(int stackTraceSerialNum, int threadSerialNum, int numFrames, long[] stackFrameIds) {
        this.stackTraceSerialNum = stackTraceSerialNum;
        this.threadSerialNum = threadSerialNum;
        this.numFrames = numFrames;
        this.stackFrameIds = stackFrameIds;
    }

    public List<HprofFrame> getFrames() {
        List<HprofFrame> frames = new ArrayList<>();
        for (long stackFrameId : stackFrameIds) {
            frames.add(HprofFrame.frameMap.get(stackFrameId));
        }
        return frames;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(info());
        sb.append("stack frames:");
        for (long sfi : stackFrameIds) {
            sb.append("        " + sfi + "\n");
            sb.append("        " + HprofFrame.frameMap.get(sfi).toString() + "\n");
        }
        return sb.toString();
    }

    public String info() {
        return "Trace:" + stackTraceSerialNum + "[" + numFrames + "]";
    }
}
