package com.hunliji.marrybiz.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.Article;
import com.hunliji.marrybiz.model.Photo;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.widget.RecyclingImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suncloud on 2015/11/20.
 */
public class ArticlesAdapter extends BaseAdapter {

    private ArrayList<Article> articles;
    private ArrayList<Integer> positions;
    private Context context;
    private LayoutInflater inflater;
    private int width;
    private int imageWidth;
    private int d;


    public ArticlesAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.articles = new ArrayList<>();
        this.positions = new ArrayList<>();
        Point point = JSONUtil.getDeviceSize(context);
        d = Math.round(context.getResources()
                .getDisplayMetrics().density * 8);
        imageWidth = width = point.x;
        if (point.x > 805) {
            imageWidth = width * 3 / 4;
        }
    }

    public void setArticles(ArrayList<Article> articles) {
        if (articles != null) {
            this.articles.clear();
            this.positions.clear();
            this.articles.addAll(articles);
            for (Article article : articles) {
                int position = articles.indexOf(article);
                if (article.getImages() != null && article.getImages()
                        .size() > 1) {
                    for (Photo ignored : article.getImages()) {
                        positions.add(position);
                    }
                } else {
                    positions.add(position);
                }
            }
        }
    }

    @Override
    public int getCount() {
        return positions.size();
    }

    @Override
    public Object getItem(int position) {
        int index = positions.get(position);
        Article article = articles.get(index);
        if (position == positions.indexOf(index)) {
            return article;
        } else {
            return article.getImages()
                    .get(position - positions.indexOf(index));
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.article_item_view, null);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        Object item = getItem(position);
        holder.bottomView.setVisibility(position + 1 < getCount() ? View.GONE : View.VISIBLE);
        if (item != null) {
            Photo image = null;
            if (item instanceof Article) {
                Article article = (Article) item;
                holder.articleLayout.setVisibility(View.VISIBLE);
                holder.name.setText(article.getName());
                holder.info.setText(article.getDescribe());
                holder.icon.setImageResource(article.getId() > 0 ? R.drawable.icon_star_round_yellow_26_26 :
                        R.drawable.icon_location_round_yellow_26_26);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder
                        .articleLayout.getLayoutParams();
                if (article.getImages() != null && !article.getImages()
                        .isEmpty()) {
                    image = article.getImages()
                            .get(0);
                    params.bottomMargin = d;
                } else {
                    params.bottomMargin = 0;
                }
            } else if (item instanceof Photo) {
                holder.articleLayout.setVisibility(View.GONE);
                image = (Photo) item;
            }
            String url = null;
            if (image != null) {
                url = JSONUtil.getImagePath(image.getImagePath(), imageWidth);
            }
            if (!JSONUtil.isEmpty(url)) {
                holder.imageView.setVisibility(View.VISIBLE);
                if (image.getHeight() > 0 && image.getWidth() > 0) {
                    holder.imageView.getLayoutParams().height = Math.round(width * image
                            .getHeight() / image.getWidth());
                }
                final Photo finalImage = image;
                ImageLoadTask task = new ImageLoadTask(holder.imageView,
                        new OnHttpRequestListener() {
                            @Override
                            public void onRequestCompleted(Object obj) {
                                if (finalImage.getHeight() == 0 || finalImage.getWidth() == 0) {
                                    Bitmap bitmap = (Bitmap) obj;
                                    if (bitmap != null) {
                                        holder.imageView.getLayoutParams().height = Math.round
                                                (width * bitmap.getHeight() / bitmap.getWidth());
                                    }
                                }
                            }

                            @Override
                            public void onRequestFailed(Object obj) {

                            }
                        });
                holder.imageView.setTag(url);
                task.loadImage(url,
                        imageWidth,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(context.getResources(),
                                R.mipmap.icon_empty_image,
                                task));
            } else {
                holder.imageView.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'article_item_view.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     * .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.info)
        TextView info;
        @BindView(R.id.article_layout)
        LinearLayout articleLayout;
        @BindView(R.id.image_view)
        RecyclingImageView imageView;
        @BindView(R.id.bottom_view)
        View bottomView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
