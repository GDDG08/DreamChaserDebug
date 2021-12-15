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
import com.zzh.dreamchaser.debugBT.tool.Interpolation;

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
            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = -200;
            v.top = 300;
            v.left = -100;
            v.right = maxnum - 1+100;
            chart.setMaximumViewport(v);
            chart.setCurrentViewport(v);
        }

        private float data_min = Float.POSITIVE_INFINITY, data_max = Float.NEGATIVE_INFINITY;
        private float data_min_cur = Float.POSITIVE_INFINITY, data_max_cur = Float.NEGATIVE_INFINITY;

        int maxnum = 10;
        private void processData() {
            data_min = 0;
            data_max = 100;
            float[] data_rec = new float[maxnum];
            for (int j = 0; j < maxnum; ++j) {
                data_rec[j] = (float) Math.random() * 50f;
            }

            List<Interpolation.Point> points = new ArrayList<>();
            List<Line> lines = new ArrayList<Line>();
            List<PointValue> values = new ArrayList<>();
            for (int j = 0; j < maxnum; j++) {
                values.add(new PointValue(j, data_rec[j]));
                points.add(new Interpolation.Point(j, data_rec[j]));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[0]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(false);
            line.setHasPoints(true);
//                line.setHasGradientToTransparent(hasGradientToTransparent);
            if (pointsHaveDifferentColor) {
                line.setPointColor(ChartUtils.COLORS[(0 + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);

            Interpolation.NewtonItp myNewtItp = new Interpolation.NewtonItp(points, maxnum);
            myNewtItp.calPolynomial();
            values = new ArrayList<>();
            for (float j = -10; j < maxnum+10; j+=0.01) {
                values.add(new PointValue(j, (float) myNewtItp.getInterpolation(j)));
            }

            line = new Line(values);
            line.setColor(ChartUtils.COLORS[1]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
//                line.setHasGradientToTransparent(hasGradientToTransparent);
            if (pointsHaveDifferentColor) {
                line.setPointColor(ChartUtils.COLORS[(1 + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);

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
            resetViewport();
        }

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
            }
        }
    }
}
