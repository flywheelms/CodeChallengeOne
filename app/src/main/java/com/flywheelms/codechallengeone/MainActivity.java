package com.flywheelms.codechallengeone;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/*
    Find a subset of the number list which totals the target number.
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.page_info)
    TextView mPageInfoView;
    @BindView(R.id.number_list)
    EditText mNumberListView;
    @BindView(R.id.target_number)
    EditText mTargetNumberView;
    @BindView(R.id.initialize_with_test_data_button)
    Button mInitializeWithTestDataButton;
    @BindView(R.id.clear_data_button)
    Button mClearDataButton;
    @BindView(R.id.possible_permutations)
    EditText mPossiblePermutationsView;
    @BindView(R.id.optimised_permutations)
    EditText mOptimisedPermutationsView;
    @BindView(R.id.elapsed_time)
    EditText mElapsedTimeView;
    @BindView(R.id.successful_permutation)
    EditText mSuccessfulPermutationView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton mFloatingActionButton;
    private ArrayList<Integer> mIntegerArrayList = new ArrayList<>();
    private int mTargetNumber;
    private String mErrorReason;
    private int mOptimizedPermutations = 0;

    private static final String TEST_NUMBER_SET =
            "18897109,12828837,9461105,6371773,5965343,5946800,5582170,5564635,5268860,"+
            "4552402,4335391,4296250,4224851,4192887,3439809,3279833,3095313,2812896,2783243,"+
            "2710489,2543482,2356285,2226009,2149127,2142508,2134411";
    private static final String TEST_TARGET_NUMBER = "33951955";
//    private static final String TEST_TARGET_NUMBER = "100000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.setDebug(BuildConfig.DEBUG);
        ButterKnife.bind(this);
        initializeViews();
    }

    @SuppressWarnings("deprecation")
    private void initializeViews() {
        if (Build.VERSION.SDK_INT <24 ) {
            mPageInfoView.setText(Html.fromHtml(getString(R.string.page_info)));
        } else {
            mPageInfoView.setText(Html.fromHtml(getString(R.string.page_info), Html.FROM_HTML_MODE_LEGACY));
        }
    }

    @OnClick(R.id.initialize_with_test_data_button)
    void onClickInitializeWithTestDataButton() {
        mNumberListView.setText(TEST_NUMBER_SET);
        mTargetNumberView.setText(TEST_TARGET_NUMBER);
    }

    @OnClick(R.id.clear_data_button)
    void onClickClearDataButton() {
        mNumberListView.setText("");
        mTargetNumberView.setText("");
        clearResults();
    }

    private void clearResults() {
        mPossiblePermutationsView.setText("");
        mOptimisedPermutationsView.setText("");
        mOptimizedPermutations = 0;
        mElapsedTimeView.setText("");
        mErrorReason = "";
        mSuccessfulPermutationView.setText("");
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.floatingActionButton)
    void onClickFloatingActionButton() {
        mProgressBar.setVisibility(View.VISIBLE);
        clearResults();
        if (missingData()) {
            return;
        }
        GuiHelper.hideSoftKeyboard(this);
        Long elapsedTime = System.currentTimeMillis();
        initializeIntegerList();
        mTargetNumber = Integer.valueOf(mTargetNumberView.getText().toString());
        mPossiblePermutationsView.setText(determinePossiblePermutations());
        if (solutionIsPossible()) {
            startLooking();
        } else {
            mSuccessfulPermutationView.setText(getString(R.string.no_solution_possible) + "  " + mErrorReason);
        }
        mElapsedTimeView.setText("" + (System.currentTimeMillis() - elapsedTime));
        mProgressBar.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void startLooking() {
        if (targetInList()) {
            writePermutations(1);
            mSuccessfulPermutationView.setText(mTargetNumberView.getText());
            return;
        }
        int index = 0;
        while (index < mIntegerArrayList.size() && ! findSolution(mTargetNumber, index)) {
            ++index;
        }
        if ( index == mIntegerArrayList.size()) {
            mSuccessfulPermutationView.setText(getString(R.string.no_solution_possible));
        }
        writePermutations(mOptimizedPermutations);
    }

    private boolean findSolution(int pCurrentTargetNumber, int pCurrentIndex) {
        if (pCurrentIndex == mIntegerArrayList.size()) {
            return false;
        }
        ++mOptimizedPermutations;
        if (mOptimizedPermutations % 100 == 0) {
            writePermutations(mOptimizedPermutations);
        }
        int remainder = pCurrentTargetNumber - mIntegerArrayList.get(pCurrentIndex);
        if (remainder == 0) {
            writePermutationElement(pCurrentIndex);
            return true;
        }
        int nextIndex = getNextIndex(remainder, pCurrentIndex);
        boolean isSolution = false;
        while (nextIndex < mIntegerArrayList.size() && ! isSolution) {
            isSolution = findSolution(remainder, nextIndex);
            nextIndex = getNextIndex(remainder, pCurrentIndex);
        }
        if (isSolution) {
            writePermutationElement(pCurrentIndex);
        }
        return isSolution;
    }

    private boolean targetInList() {
        for (Integer integer : mIntegerArrayList) {
            if (integer.equals(mTargetNumber)) {
                return true;
            }
        }
        return false;
    }

    private boolean missingData() {
        if (mNumberListView.getText().length() < 1 || mTargetNumberView.getText().length() < 1) {
            Toast.makeText(this, "Missing Input Data", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private boolean solutionIsPossible() {
        boolean isPossible = true;
        if (mTargetNumber < mIntegerArrayList.get(mIntegerArrayList.size() - 1)) {
            mErrorReason = getString(R.string.target_smaller_than_smallest_number);
            isPossible = false;
        } else {
            Integer sumArray = 0;
            for (Integer integer: mIntegerArrayList) {
                sumArray += integer;
            }
            if (sumArray < mTargetNumber) {
                mErrorReason = getString(R.string.target_larger_than_sum_of_number_list);
                isPossible = false;
            }
        }
        if (! isPossible) {
            writePermutations(0);
        }
        return isPossible;
    }

    @SuppressLint("SetTextI18n")
    private void writePermutations(int pPermutations) {
        mOptimisedPermutationsView.setText("" + pPermutations);
    }

    private void initializeIntegerList() {
        mIntegerArrayList.clear();
        StringTokenizer stringTokenizer = new StringTokenizer(mNumberListView.getText().toString(), ",");
        while (stringTokenizer.hasMoreTokens()) {
            mIntegerArrayList.add(Integer.valueOf(stringTokenizer.nextToken()));
        }
        Collections.sort(mIntegerArrayList, new IntegerComparator());
    }

    private String determinePossiblePermutations() {
        int permutationCount = mIntegerArrayList.size() > 0 ?
                (int) Math.round(Math.pow(2, mIntegerArrayList.size())) -1 :
                0;
        return NumberFormat.getNumberInstance(Locale.US).format(permutationCount);
    }

    private class IntegerComparator implements Comparator<Integer> {

        @SuppressWarnings("SuspiciousNameCombination")
        @Override
        public int compare(Integer pIntegerLeft, Integer pIntegerRight) {
            return Integer.compare(pIntegerRight, pIntegerLeft);   // largest to smallest
        }
    }

    private int getNextIndex(int pCurrentTarget, int pCurrentIndex) {
        for (int index = pCurrentIndex + 1 ; index < mIntegerArrayList.size() ; ++index) {
            if (mIntegerArrayList.get(index) <= pCurrentTarget) {
                return index;
            }
        }
        return mIntegerArrayList.size();
    }

    private void writePermutationElement(int pIndex) {
        if (! mSuccessfulPermutationView.getText().toString().isEmpty()) {
            mSuccessfulPermutationView.append(", ");
        }
        mSuccessfulPermutationView.append("" + mIntegerArrayList.get(pIndex));
    }

}
