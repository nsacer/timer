package zhao.pary.timer;

/**
 * 通过key-value形式存储到SharedPreference，这里的都是key
 */

public class TimerSetting {

    /**
     * 屏幕的宽度、高度
     */
    static final String DISPLAY_WIDTH = "display_width";
    static final String DISPLAY_HEIGHT = "display_height";

    /**
     * 密码相关
     */
    public static final String GESTURE_PASSWORD = "gesture_password";
    public static final int REQUEST_CODE_OPEN_GESTURE_PWD = 0x001;
    public static final int REQUEST_CODE_CLOSE_GESTURE_PWD = 0x002;
    public static final int RESULT_CODE_OPEN_GESTURE_PWD = 0x003;
    public static final int RESULT_CODE_CLOSE_GESTURE_PWD = 0x004;
    public static final String IS_CLOSE_GESTURE_PASSWORD = "close_gesture_password";
    public static boolean IS_GESTURE_PASSWORD_PASS = false;

    /**
     * 启动页获取到OneWord发送携带Bundle的Handler what
     */
    public static final int HANDLER_ONE_WORD = 0x005;

    /**
     * 从MainActivity点击itemCard跳转到落地页的传递的Target的id的key
     */
    public static final String TARGET_TO_DETAIL_ID = "target_id";

    /**
     * 内存存储数据库是否有更新，用来在返回到MainActivity（onResume）时候判断是否重新读取数据库数据
     * */
    public static boolean DB_UPDATE = Boolean.FALSE;
}
