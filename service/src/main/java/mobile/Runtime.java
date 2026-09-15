package mobile;

import go.Seq;
import java.util.Arrays;

/* JADX INFO: compiled from: r8-map-id-707d2408c1f72033c5f37f8a7babebbe4683766865b0a7a99a7e9b43cd86f48b */
/* JADX INFO: loaded from: classes.dex */
public final class Runtime implements Seq.Proxy {
    private final int refnum;

    static {
        Mobile.touch();
    }

    public Runtime() {
        int i__New = __New();
        this.refnum = i__New;
        Seq.trackGoRef(i__New, this);
    }

    private static native int __New();

    public native long check(String str, String str2, String str3, String str4, String str5, long j, long j2, long j3, long j4);

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Runtime)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[0]);
    }

    @Override // go.Seq.GoObject
    public final int incRefnum() {
        Seq.incGoRef(this.refnum, this);
        return this.refnum;
    }

    public native boolean isRunning();

    public native long ping(String str, String str2, String str3, String str4, String str5, long j, long j2, String str6, long j3, long j4);

    public native void setChannel(String str);

    public native void setDNS(String str);

    public native void setDebug(boolean z);

    public native void setDeviceID(String str);

    public native void setDeviceIDPath(String str);

    public native void setEngine(String str, String str2, String str3);

    public native void setKey(String str);

    public native void setLivenessOptions(long j, long j2, long j3);

    public native void setLogWriter(LogWriter logWriter);

    public native void setProtector(SocketProtector socketProtector);

    public native void setProvider(String str);

    public native void setProviderToken(String str);

    public native void setRoom(String str);

    public native void setSEIOptions(long j, long j2, long j3, long j4);

    public native void setSocksCredentials(String str, String str2);

    public native void setSocksListenHost(String str);

    public native void setSocksPort(long j);

    public native void setTrafficOptions(long j, long j2, long j3);

    public native void setTransport(String str);

    public native void setVP8Options(long j, long j2);

    public native void setVideoOptions(long j, long j2, long j3, long j4, String str, String str2, long j5, long j6);

    public native void start();

    public native String state();

    public native void stop(long j);

    public String toString() {
        return "Runtime{}";
    }

    public native void waitReady(long j);

    public Runtime(int i) {
        this.refnum = i;
        Seq.trackGoRef(i, this);
    }
}
