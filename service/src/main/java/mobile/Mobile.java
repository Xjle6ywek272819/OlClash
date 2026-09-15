package mobile;

import go.Seq;

/* JADX INFO: compiled from: r8-map-id-707d2408c1f72033c5f37f8a7babebbe4683766865b0a7a99a7e9b43cd86f48b */
/* JADX INFO: loaded from: classes.dex */
public abstract class Mobile {

    /* JADX INFO: compiled from: r8-map-id-707d2408c1f72033c5f37f8a7babebbe4683766865b0a7a99a7e9b43cd86f48b */
    public static final class proxyLogWriter implements Seq.Proxy, LogWriter {
        private final int refnum;

        public proxyLogWriter(int i) {
            this.refnum = i;
            Seq.trackGoRef(i, this);
        }

        @Override // go.Seq.GoObject
        public final int incRefnum() {
            Seq.incGoRef(this.refnum, this);
            return this.refnum;
        }

        @Override // mobile.LogWriter
        public native void writeLog(String str);
    }

    /* JADX INFO: compiled from: r8-map-id-707d2408c1f72033c5f37f8a7babebbe4683766865b0a7a99a7e9b43cd86f48b */
    public static final class proxySocketProtector implements Seq.Proxy, SocketProtector {
        private final int refnum;

        public proxySocketProtector(int i) {
            this.refnum = i;
            Seq.trackGoRef(i, this);
        }

        @Override // go.Seq.GoObject
        public final int incRefnum() {
            Seq.incGoRef(this.refnum, this);
            return this.refnum;
        }

        @Override // mobile.SocketProtector
        public native boolean protect(long j);
    }

    static {
        Seq.touch();
        _init();
    }

    private Mobile() {
    }

    private static native void _init();

    public static native Exception getErrAlreadyRunning();

    public static native Exception getErrHTTPPingTimeout();

    public static native Exception getErrInvalidConfig();

    public static native Exception getErrNotRunning();

    public static native Exception getErrReadyTimeout();

    public static native Exception getErrStopTimeout();

    public static native Exception getErrStoppedBeforeReady();

    public static native Exception getErrUnexpectedHTTPStatus();

    public static native Exception getErrUnsupportedProvider();

    public static native Exception getErrUnsupportedTransport();

    public static native Runtime new_();

    public static native void setErrAlreadyRunning(Exception exc);

    public static native void setErrHTTPPingTimeout(Exception exc);

    public static native void setErrInvalidConfig(Exception exc);

    public static native void setErrNotRunning(Exception exc);

    public static native void setErrReadyTimeout(Exception exc);

    public static native void setErrStopTimeout(Exception exc);

    public static native void setErrStoppedBeforeReady(Exception exc);

    public static native void setErrUnexpectedHTTPStatus(Exception exc);

    public static native void setErrUnsupportedProvider(Exception exc);

    public static native void setErrUnsupportedTransport(Exception exc);

    public static void touch() {
    }
}
