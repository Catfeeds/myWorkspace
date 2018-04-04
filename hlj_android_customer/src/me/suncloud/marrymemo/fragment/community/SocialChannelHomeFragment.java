package me.suncloud.marrymemo.fragment.community;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.SocialChannelHomeAdapter;
import me.suncloud.marrymemo.adpter.community.viewholder
        .SocialChannelHomeRecommendChannelViewHolder;
import me.suncloud.marrymemo.api.community.CommunityApi;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.CommunityTogglesUtil;
import me.suncloud.marrymemo.util.Session;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * 新娘圈fragment
 * Created by jinxin on 2018/3/13 0013.
 */

public class SocialChannelHomeFragment extends ScrollAbleFragment implements
        SocialChannelHomeRecommendChannelViewHolder.onJoinClickListener, PullToRefreshBase
        .OnRefreshListener {

    @Override
    public String fragmentPageTrackTagName() {
        return "新娘圈";
    }

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    Unbinder unbinder;

    private HljHttpSubscriber authSub;
    private View footerView;
    private LinearLayoutManager layoutManager;
    private SocialChannelHomeAdapter homeAdapter;
    private User user;

    public static SocialChannelHomeFragment newInstance() {
        SocialChannelHomeFragment channelFragment = new SocialChannelHomeFragment();
        return channelFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        footerView = LayoutInflater.from(getContext())
                .inflate(R.layout.hlj_foot_no_more___cm, null, false);
        footerView.findViewById(R.id.no_more_hint)
                .setVisibility(View.VISIBLE);
        footerView.findViewById(R.id.loading)
                .getLayoutParams().height = CommonUtil.dp2px(getContext(), 50);
        footerView.findViewById(R.id.loading)
                .setVisibility(View.INVISIBLE);
        layoutManager = new LinearLayoutManager(getContext());
        homeAdapter = new SocialChannelHomeAdapter(getContext());
        homeAdapter.setFooterView(footerView);
        homeAdapter.setOnJoinClickListener(this);
        user = Session.getInstance()
                .getCurrentUser(getContext());
        EventBus.getDefault()
                .register(this);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        initWidget();
        return rootView;
    }

    private void initWidget() {
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(homeAdapter);
        recyclerView.setHeaderColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        recyclerView.setOnRefreshListener(this);
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                onRefresh(null);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoad();
    }

    private void initLoad() {
        onRefresh(null);
    }

    private void initAuthLoad(PullToRefreshBase refreshBase) {
        CommonUtil.unSubscribeSubs(authSub);
        User user = Session.getInstance()
                .getCurrentUser(getContext());
        authSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setEmptyView(emptyView)
                .setPullToRefreshBase(recyclerView)
                .setContentView(recyclerView)
                .setProgressBar(refreshBase == null ? progressBar : null)
                .setOnNextListener(new SubscriberOnNextListener<ChannelListResultZip>() {
                    @Override
                    public void onNext(ChannelListResultZip channelListResultZip) {
                        setAuthResult(channelListResultZip);
                    }
                })
                .build();
        Observable<HljHttpData<List<CommunityChannel>>> myChannelListObb = Observable.just(user
                == null ? 0L : user.getId())
                .flatMap(new Func1<Long, Observable<HljHttpData<List<CommunityChannel>>>>() {
                    @Override
                    public Observable<HljHttpData<List<CommunityChannel>>> call(Long userId) {
                        if (userId <= 0L) {
                            HljHttpData<List<CommunityChannel>> empty = new HljHttpData<>();
                            empty.setData(null);
                            return Observable.just(empty);
                        }
                        return CommunityApi.myChannelList();
                    }
                });
        Observable<HljHttpData<List<CommunityChannel>>> recommendListObb = CommunityApi
                .recommendChannelList();
        Observable<ChannelListResultZip> zipObb = Observable.zip(myChannelListObb,
                recommendListObb,
                new Func2<HljHttpData<List<CommunityChannel>>, HljHttpData<List<CommunityChannel>>,
                        ChannelListResultZip>() {
                    @Override
                    public ChannelListResultZip call(
                            HljHttpData<List<CommunityChannel>> myChannelList,
                            HljHttpData<List<CommunityChannel>> recommendList) {
                        ChannelListResultZip zip = new ChannelListResultZip();
                        zip.myChannelList = myChannelList;
                        zip.recommendList = recommendList;
                        return zip;
                    }
                });
        zipObb.subscribe(authSub);
    }

    private void setAuthResult(ChannelListResultZip zip) {
        if (zip.myChannelList != null) {
            homeAdapter.setMyChannelList(zip.myChannelList.getData());
        }
        if (zip.recommendList != null) {
            homeAdapter.setRecommendChannelList(zip.recommendList.getData());
        }
        homeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        initAuthLoad(refreshView);
    }

    @Override
    public void refresh(Object... params) {
        onRefresh(null);
    }

    /**
     * 退出登录或者切换用户之后，刷新数据
     *
     * @param u
     */
    public void userRefresh(User u) {
        if (u != null) {
            if (user != null) {
                if (!u.getId()
                        .equals(user.getId())) {
                    user = u;
                    scrollTop();
                    refresh();
                }
            } else {
                user = u;
                scrollTop();
                refresh();
            }
        } else if (user != null) {
            user = null;
            scrollTop();
            refresh();
        }
    }

    private void scrollTop() {
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        if (firstPosition > 20) {
            recyclerView.getRefreshableView()
                    .scrollToPosition(10);
            recyclerView.getRefreshableView()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.getRefreshableView()
                                    .smoothScrollToPosition(0);
                        }
                    }, 200);
        } else {
            recyclerView.getRefreshableView()
                    .smoothScrollToPosition(0);
        }
    }

    @Override
    public void onJoinClick(CommunityChannel channel) {
        if (!AuthUtil.loginBindCheck(getContext())) {
            return;
        }
        if (channel == null) {
            return;
        }
        if (!channel.isFollowed()) {
            CommunityTogglesUtil.onCommunityChannelFollow(getActivity(),
                    channel.getId(),
                    channel,
                    null,
                    null);
        }
    }

    public void onEvent(MessageEvent event) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (event == null) {
            return;
        }
        if (event.getType() == MessageEvent.EventType.COMMUNITY_CHANNEL_FOLLOW_FLAG) {
            onRefresh(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(authSub);
        EventBus.getDefault()
                .unregister(this);
    }

    @Override
    public View getScrollableView() {
        return recyclerView.getRefreshableView();
    }

    class ChannelListResultZip extends HljHttpResultZip {
        @HljRZField
        HljHttpData<List<CommunityChannel>> myChannelList;
        @HljRZField
        HljHttpData<List<CommunityChannel>> recommendList;
    }
}
