package com.zzh.dreamchaser.debugBT;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smarx.notchlib.NotchScreenManager;
import com.zzh.dreamchaser.debugBT.data.Var;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;

import static com.zzh.dreamchaser.debugBT.MainActivity.mContent;
import static com.zzh.dreamchaser.debugBT.view.SimpleScopeView.MAX_REX;

public class ScopeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scope);
        NotchScreenManager.getInstance().setDisplayInNotch(this);

        int[] watch_list = getIntent().getExtras().getIntArray("watch_list");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ChartholderFragment(watch_list)).commit();
        }
    }

    public static class ChartholderFragment extends Fragment {

        private int[] watch_list = new int[]{};

        private LineChartView chart;
        private LineChartData data;

        final private int DEFAULT_CURRENT_VIEW_POINTS_NUM = 1000;

        private boolean hasAxes = true;
        private boolean hasAxesNames = true;
        private boolean hasLines = true;
        private boolean hasPoints = false;
        private ValueShape shape = ValueShape.CIRCLE;
        private boolean isFilled = false;
        private boolean hasLabels = false;
        private boolean isCubic = false;
        private boolean hasLabelForSelected = true;
        private boolean pointsHaveDifferentColor = false;
        //        private boolean hasGradientToTransparent = false;
        private Float[] timeStamp_his;

        public ChartholderFragment(int[] watch_list) {
            this.watch_list = watch_list;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);

            chart = (LineChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ValueTouchListener());

            processData();

            chart.setViewportCalculationEnabled(false);

            resetViewport();

            Switch switch_cubic = (Switch) rootView.findViewById(R.id.switch_cubic);
            switch_cubic.setOnCheckedChangeListener((v, ischeck) -> {
                isCubic = !ischeck;
                toggleCubic();
            });

            RadioGroup radio_zoom = (RadioGroup) rootView.findViewById(R.id.radio_zoom);
            radio_zoom.setOnCheckedChangeListener((v, pos) -> {
                switch (pos) {
                    case R.id.radioButton:
                        chart.setZoomType(ZoomType.HORIZONTAL);
                        break;
                    case R.id.radioButton2:
                        chart.setZoomType(ZoomType.VERTICAL);
                        break;
                    case R.id.radioButton3:
                        chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
                        break;
                }
            });

            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.scope_fab);
            fab.setOnClickListener((v) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("请选择变量(<=5)");
                final String[] items = (String[]) mContent.tagList.toArray(new String[0]);
                final boolean[] checked = new boolean[200];
                builder.setMultiChoiceItems(items, checked, (dialog, which, isChecked) -> {
//                    Toast.makeText(getContext(), items[which] + isChecked, Toast.LENGTH_SHORT).show();
                    checked[which] = isChecked;
                });

                builder.setPositiveButton("确定", (dialog, which) -> {
                    List<Integer> watch_list_new = new ArrayList<>();
                    for (int i = 0; i < checked.length && i < 5; i++)
                        if (checked[i])
                            watch_list_new.add(i);
                    int[] watch_list_new2 = new int[watch_list_new.size()];
                    for (int i = 0; i < watch_list_new.size(); i++)
                        watch_list_new2[i] = watch_list_new.get(i);
                    watch_list = watch_list_new2;

                    processData();
                    resetViewport();
                });
                builder.show();
            });
            return rootView;
        }

        private void resetViewport() {
            if (timeStamp_his.length == 0)
                return;
            // Reset viewport height range to (0,100)
            Viewport v1 = new Viewport(chart.getMaximumViewport());
            v1.bottom = data_min - 5;
            v1.top = data_max + 5;
            v1.left = timeStamp_his[0];
            v1.right = timeStamp_his[timeStamp_his.length - 1];
            chart.setMaximumViewport(v1);

            Viewport v2 = new Viewport(chart.getMaximumViewport());
            v2.bottom = data_min_cur - 1;
            v2.top = data_max_cur + 1;
            if (timeStamp_his.length > DEFAULT_CURRENT_VIEW_POINTS_NUM)
                v2.left = timeStamp_his[timeStamp_his.length - DEFAULT_CURRENT_VIEW_POINTS_NUM];
            else
                v2.left = timeStamp_his[0];
            v2.right = timeStamp_his[timeStamp_his.length - 1];
            chart.setCurrentViewport(v2);
        }

        private float data_min = Float.POSITIVE_INFINITY, data_max = Float.NEGATIVE_INFINITY;
        private float data_min_cur = Float.POSITIVE_INFINITY, data_max_cur = Float.NEGATIVE_INFINITY;

        private void processData() {
            data_min = Float.POSITIVE_INFINITY;
            data_max = Float.NEGATIVE_INFINITY;

            Var timeStamp = (Var) mContent.list.get(0);
            timeStamp_his = timeStamp.history.toArray(new Float[0]);
            List<Line> lines = new ArrayList<Line>();
            for (int i = 0; i < watch_list.length; i++) {
                Var data = (Var) mContent.list.get(watch_list[i]);
                Float[] data_his = data.history.toArray(new Float[0]);

                if (data_his.length == 0)
                    continue;
                for (float his : data_his) {
                    data_min = Float.min(data_min, his);
                    data_max = Float.max(data_max, his);
                }

                for (int k = 1; k < data_his.length && k < DEFAULT_CURRENT_VIEW_POINTS_NUM; k++) {
                    data_min_cur = Float.min(data_min_cur, data_his[data_his.length - k]);
                    data_max_cur = Float.max(data_max_cur, data_his[data_his.length - k]);
                }

                List<PointValue> values = new ArrayList<PointValue>();
                for (int j = 0; j < MAX_REX && j < timeStamp_his.length; j++) {
                    values.add(new PointValue(timeStamp_his[j], data_his[j]));
                }

                Line line = new Line(values);
                line.setColor(ChartUtils.COLORS[i]);
                line.setShape(shape);
                line.setCubic(isCubic);
                line.setFilled(isFilled);
                line.setHasLabels(hasLabels);
                line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                line.setHasLines(hasLines);
                line.setHasPoints(hasPoints);
//                line.setHasGradientToTransparent(hasGradientToTransparent);
                if (pointsHaveDifferentColor) {
                    line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
                }
                lines.add(line);
            }

            data = new LineChartData(lines);

            if (hasAxes) {
                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
//                    axisX.setName("Axis X");
                    axisY.setName(mContent.tagList.get(watch_list[0]) + "");
                }
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            data.setBaseValue(Float.NEGATIVE_INFINITY);
            chart.setLineChartData(data);

        }

        private void toggleCubic() {
            isCubic = !isCubic;

            processData();

//            if (isCubic) {
//                // It is good idea to manually set a little higher max viewport for cubic lines because sometimes line
//                // go above or below max/min. To do that use Viewport.inest() method and pass negative value as dy
//                // parameter or just set top and bottom values manually.
//                // In this example I know that Y values are within (0,100) range so I set viewport height range manually
//                // to (-5, 105).
//                // To make this works during animations you should use Chart.setViewportCalculationEnabled(false) before
//                // modifying viewport.
//                // Remember to set viewport after you call setLineChartData().
//                //TODO: change
//                final Viewport v = new Viewport(chart.getMaximumViewport());
//                v.bottom = -5;
//                v.top = 105;
//                // You have to set max and current viewports separately.
//                chart.setMaximumViewport(v);
//                // I changing current viewport with animation in this case.
//                chart.setCurrentViewportWithAnimation(v);
//            } else {
//                // If not cubic restore viewport to (0,100) range.
//                final Viewport v = new Viewport(chart.getMaximumViewport());
//                v.bottom = 0;
//                v.top = 100;
//
//                // You have to set max and current viewports separately.
//                // In this case, if I want animation I have to set current viewport first and use animation listener.
//                // Max viewport will be set in onAnimationFinished method.
//                chart.setViewportAnimationListener(new ChartAnimationListener() {
//
//                    @Override
//                    public void onAnimationStarted() {
//                        // TODO Auto-generated method stub
//
//                    }
//
//                    @Override
//                    public void onAnimationFinished() {
//                        // Set max viewpirt and remove listener.
//                        chart.setMaximumViewport(v);
//                        chart.setViewportAnimationListener(null);
//
//                    }
//                });
//                // Set current viewpirt with animation;
//                chart.setCurrentViewportWithAnimation(v);
//            }
            resetViewport();

        }

//        private void toggleFilled() {
//            isFilled = !isFilled;
//
//            generateData();
//        }
//
//        private void togglePointColor() {
//            pointsHaveDifferentColor = !pointsHaveDifferentColor;
//
//            generateData();
//        }
//
//        private void setCircles() {
//            shape = ValueShape.CIRCLE;
//
//            generateData();
//        }
//
//        private void setSquares() {
//            shape = ValueShape.SQUARE;
//
//            generateData();
//        }
//
//        private void setDiamonds() {
//            shape = ValueShape.DIAMOND;
//
//            generateData();
//        }
//
//        private void toggleLabels() {
//            hasLabels = !hasLabels;
//
//            if (hasLabels) {
//                hasLabelForSelected = false;
//                chart.setValueSelectionEnabled(hasLabelForSelected);
//            }
//
//            generateData();
//        }
//
//        private void toggleLabelForSelected() {
//            hasLabelForSelected = !hasLabelForSelected;
//
//            chart.setValueSelectionEnabled(hasLabelForSelected);
//
//            if (hasLabelForSelected) {
//                hasLabels = false;
//            }
//
//            generateData();
//        }
//
//        private void toggleAxes() {
//            hasAxes = !hasAxes;
//
//            generateData();
//        }
//
//        private void toggleAxesNames() {
//            hasAxesNames = !hasAxesNames;
//
//            generateData();
//        }

        /**
         * To animate values you have to change targets values and then call {@link Chart#startDataAnimation()}
         * method(don't confuse with View.animate()). If you operate on data that was set before you don't have to call
         * {@link LineChartView#setLineChartData(LineChartData)} again.
         */
        private void prepareDataAnimation() {
            for (Line line : data.getLines()) {
                for (PointValue value : line.getValues()) {
                    // Here I modify target only for Y values but it is OK to modify X targets as well.
                    value.setTarget(value.getX(), (float) Math.random() * 100);
                }
            }
        }

        private class ValueTouchListener implements LineChartOnValueSelectListener {

            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }

        }
    }
}
