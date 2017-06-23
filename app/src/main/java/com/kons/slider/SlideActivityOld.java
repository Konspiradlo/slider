//package com.kons.slider;
//
//import android.os.Bundle;
//import android.os.SystemClock;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//
//import ioio.lib.api.DigitalInput;
//import ioio.lib.api.exception.ConnectionLostException;
//import ioio.lib.util.BaseIOIOLooper;
//import ioio.lib.util.IOIOLooper;
//import ioio.lib.util.android.IOIOActivity;
//
//public class SlideActivityOld extends IOIOActivity {
//
//    private final int RIGHTARROW_PIN = 34;
//    private final int LEFTARROW_PIN = 35;
//    private final int UPARROW_PIN = 36;
//    private final int DOWNARROW_PIN = 37;
//
//    private TextView rightArrowTextView;
//    private TextView leftArrowTextView;
//    private TextView upArrowTextView;
//    private TextView downArrowTextView;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_slide);
//
////        rightArrowTextView = (TextView) findViewById(R.id.rightArrowTextView);
////        leftArrowTextView = (TextView) findViewById(R.id.rightArrowTextView);
////        upArrowTextView = (TextView) findViewById(R.id.upArrowTextView);
////        downArrowTextView = (TextView) findViewById(R.id.downArrowTextView);
//    }
//
//    class Looper extends BaseIOIOLooper {
//
//        private DigitalInput rightArrow;
//        private DigitalInput leftArrow;
//        private DigitalInput upArrow;
//        private DigitalInput downArrow;
//
//        @Override
//        protected void setup() throws ConnectionLostException, InterruptedException {
//            try {
//                rightArrow = ioio_.openDigitalInput(RIGHTARROW_PIN, DigitalInput.Spec.Mode.PULL_UP);
//                leftArrow = ioio_.openDigitalInput(LEFTARROW_PIN, DigitalInput.Spec.Mode.PULL_UP);
//                upArrow = ioio_.openDigitalInput(UPARROW_PIN, DigitalInput.Spec.Mode.PULL_UP);
//                downArrow = ioio_.openDigitalInput(DOWNARROW_PIN, DigitalInput.Spec.Mode.PULL_UP);
//            } catch (ConnectionLostException e) {
//                throw e;
//            }
//        }
//
//        @Override
//        public void loop() throws ConnectionLostException, InterruptedException {
//            try {
//                int i = 0;
//                String rightArrowTxt;
//                String leftArrowTxt;
//                String upArrowTxt;
//                String downArrowTxt;
//
//
//                final boolean rightArrowReading = rightArrow.read();
//                final boolean leftArrowReading = leftArrow.read();
//                final boolean upArrowReading = upArrow.read();
//                final boolean downArrowReading = downArrow.read();
//
//                View view = findViewById(android.R.id.content);
//
//
//                if (!rightArrowReading) {
//                    rightArrowTxt = getString(R.string.rightArrow) + " active!";
//                    move(view, MotionEvent.BUTTON_FORWARD);
//                } else {
//                    rightArrowTxt = getString(R.string.rightArrow);
//                }
//                if (!leftArrowReading) {
//                    leftArrowTxt = getString(R.string.leftArrow) + " active!";
//                    move(view, MotionEvent.BUTTON_BACK);
//                } else {
//                    leftArrowTxt = getString(R.string.leftArrow);
//                }
//                if (!upArrowReading) {
//                    upArrowTxt = getString(R.string.upArrow) + " active!";
//                    move(view, MotionEvent.ACTION_POINTER_UP);
//                } else {
//                    upArrowTxt = getString(R.string.upArrow);
//                }
//                if (!downArrowReading) {
//                    downArrowTxt = getString(R.string.downArrow) + " active!";
//                    move(view, MotionEvent.ACTION_POINTER_DOWN);
//                } else {
//                    downArrowTxt = getString(R.string.downArrow);
//                }
//
//                setText(rightArrowTxt, leftArrowTxt, upArrowTxt, downArrowTxt);
//                Thread.sleep(20);
//
//                i++;
//                {
//                    if (i == 500) {
//                        i = 0;
//                        ImageView image = (ImageView) findViewById(R.id.image);
//                        for(int resourceId: getDrawables()){
//
//                        }
//                    }
//                }
//
//            } catch (InterruptedException e) {
//                ioio_.disconnect();
//            } catch (ConnectionLostException e) {
//                throw e;
//            }
//        }
//    }
//
//    private void setText(final String right, final String left, final String up, final String down) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                rightArrowTextView.setText(right);
//                leftArrowTextView.setText(left);
//                upArrowTextView.setText(up);
//                downArrowTextView.setText(down);
//            }
//        });
//    }
//
//    private void move(View view, int motionType) {
//        long downTime = SystemClock.uptimeMillis();
//        long eventTime = SystemClock.uptimeMillis() + 100;
//        float x = 0.0f;
//        float y = 0.0f;
//        int metaState = 0;
//
//        MotionEvent motionEvent = MotionEvent.obtain(
//                downTime,
//                eventTime,
//                motionType,
//                x,
//                y,
//                metaState
//        );
//        view.dispatchTouchEvent(motionEvent);
//    }
//
//    private List<Integer> getDrawables() {
//        final R.drawable drawableResources = new R.drawable();
//        final Class<R.drawable> c = R.drawable.class;
//        final Field[] fields = c.getDeclaredFields();
//        List<Integer> resourceIds = new ArrayList<Integer>();
//
//        for (int i = 0, max = fields.length; i < max; i++) {
//            final int resourceId;
//            try {
//                resourceIds.add(fields[i].getInt(drawableResources));
//            } catch (Exception e) {
//                continue;
//            }
//    /* make use of resourceId for accessing Drawables here */
//        }
//        return resourceIds;
//    }
//
//    @Override
//    protected IOIOLooper createIOIOLooper() {
//        return new Looper();
//    }
//
//}
