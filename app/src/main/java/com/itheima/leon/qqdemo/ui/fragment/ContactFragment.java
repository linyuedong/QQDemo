package com.itheima.leon.qqdemo.ui.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.leon.qqdemo.R;
import com.itheima.leon.qqdemo.adpater.ContactListAdapter;
import com.itheima.leon.qqdemo.model.ContactItem;
import com.itheima.leon.qqdemo.presenter.ContactPresenter;
import com.itheima.leon.qqdemo.presenter.impl.ContactPresenterImpl;
import com.itheima.leon.qqdemo.view.ContactView;
import com.itheima.leon.qqdemo.widget.SlideBar;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * 创建者:   Leon
 * 创建时间:  2016/10/17 22:37
 * 描述：    TODO
 */
public class ContactFragment extends BaseFragment implements ContactView {
    public static final String TAG = "ContactFragment";

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.add)
    ImageView mAdd;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.slide_bar)
    SlideBar mSlideBar;
    @BindView(R.id.section)
    TextView mSection;

    private ContactListAdapter mContactListAdapter;


    private ContactPresenter mContactPresenter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_contacts;
    }

    @Override
    protected void init() {
        super.init();
        initView();
        mContactPresenter = new ContactPresenterImpl(this);
        mContactPresenter.getContactList();
    }

    private void initView() {
        mTitle.setText(getString(R.string.contacts));
        mAdd.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSwipeRefreshLayout.setColorSchemeResources(R.color.qq_blue, R.color.qq_red);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSlideBar.setOnSlidingBarChangeListener(mOnSlideBarChangeListener);
    }

    @Override
    public void onGetContactList(List<ContactItem> list) {
        Log.d(TAG, "onGetContactList: " + list.size());
        mContactListAdapter = new ContactListAdapter(getContext(), list);
        mRecyclerView.setAdapter(mContactListAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onGetContactListFailed() {
        toast(getString(R.string.get_contacts_error));
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mContactPresenter.refreshContactList();
        }
    };

    private SlideBar.OnSlideBarChangeListener mOnSlideBarChangeListener = new SlideBar.OnSlideBarChangeListener() {
        @Override
        public void onSectionChange(int index, String section) {
            mSection.setVisibility(View.VISIBLE);
            mSection.setText(section);
            scrollToSection(section);
        }

        @Override
        public void onSlidingFinish() {
            mSection.setVisibility(View.GONE);
        }
    };

    private void scrollToSection(String section) {
//        int sectionPosition = getSectionPosition(section);
//        Log.d(TAG, "scrollToSection: " + sectionPosition);
//        mRecyclerView.scrollToPosition(sectionPosition);
//        if (sectionPosition != -1) {
//            mRecyclerView.smoothScrollToPosition(sectionPosition);
//        }
        //获取section在分区集合中索引
        String[] sections = mContactListAdapter.getSections();
        Log.d(TAG, "scrollToSection: sections size " + sections.length);
        int sectionIndex = Arrays.binarySearch(sections, section);
        //获取分区索引对应分区中第一个元素在列表中的位置
        int position = mContactListAdapter.getPositionForSection(sectionIndex);
        mRecyclerView.scrollToPosition(position);
    }


    private int getSectionPosition(String section) {
        List<ContactItem> contactItems = mContactListAdapter.getContactItems();
        for (int i = 0; i < contactItems.size(); i++) {
            if (section.equals(contactItems.get(i).getFirstLetterString())) {
                return i;
            }
        }
        return -1;
    }
}