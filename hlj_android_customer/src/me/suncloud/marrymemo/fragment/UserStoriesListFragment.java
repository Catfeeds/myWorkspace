package me.suncloud.marrymemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.models.story.Story;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.StoryAdapter;
import me.suncloud.marrymemo.api.story.StoryApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.StoryActivity;
import rx.Observable;

/**
 * Created by werther on 16/8/31.
 * 用户主页的故事列表Fragment
 */
public class UserStoriesListFragment extends ScrollAbleFragment implements StoryAdapter
        .onItemClickListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private long userId;
    private ArrayList<Story> stories;
    private Unbinder unbinder;
    private View rootView;
    private StoryAdapter adapter;
    private HljHttpSubscriber initSub;

    public static UserStoriesListFragment newInstance(long userId) {
        UserStoriesListFragment fragment = new UserStoriesListFragment();
        Bundle args = new Bundle();
        args.putLong("user_id", userId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initValues();
    }

    private void initValues() {
        userId = getArguments().getLong("user_id");

        stories = new ArrayList<>();
        adapter = new StoryAdapter(getActivity(), stories);
        View footerView = View.inflate(getActivity(), R.layout.hlj_foot_no_more___cm, null);
        adapter.setOnItemClickListener(this);

        adapter.setFooterView(footerView);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.hlj_common_fragment_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        setEmptyView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
        recyclerView.setAdapter(adapter);
        initLoad();

        return rootView;
    }

    private void initLoad() {
        Observable<HljHttpData<List<Story>>> observable = StoryApi.getStoriesObb(userId);
        initSub = HljHttpSubscriber.buildSubscriber(getActivity())
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Story>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Story>> listHljHttpData) {
                        stories.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        observable.subscribe(initSub);
    }

    public void setEmptyView() {
        User user = Session.getInstance()
                .getCurrentUser(getActivity());
        if (user != null && user.getId()
                .equals(userId)) {
            emptyView.setHintId(R.string.label_him_no_story2);
        } else {
            emptyView.setHintId(R.string.label_him_no_story);
        }
    }

    @Override
    public void refresh(Object... params) {
        if (params != null) {
            userId = (long) params[0];
        }
        setEmptyView();
        stories.clear();
        adapter.notifyDataSetChanged();
        initLoad();
    }

    @Override
    public View getScrollableView() {
        if (stories.isEmpty()) {
            return null;
        } else {
            return recyclerView;
        }
    }

    @Override
    public void onItemClick(int position, Story story) {
        if (story != null && story.getId() > 0) {
            Intent intent = new Intent(getActivity(), StoryActivity.class);
            intent.putExtra("id", story.getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (initSub != null && !initSub.isUnsubscribed()) {
            initSub.unsubscribe();
        }
    }
}
