package com.hunliji.hljquestionanswer.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.hunliji.hljquestionanswer.fragments.AnswerDetailFragment;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentPagerAdapter {

	int size;
	ArrayList<Answer> answers;
	private SparseArray<AnswerDetailFragment> fragments;

	public FragmentAdapter(FragmentManager fm,ArrayList<Answer> answers) {
		super(fm);
		this.answers = answers;
	}

	@Override
	public Fragment getItem(int position) {
		if (fragments == null) {
			fragments = new SparseArray<>();
		}
		AnswerDetailFragment fragment = fragments.get(position);
		if (fragment != null) {
			return fragment;
		}
		fragment = AnswerDetailFragment.newInstance(position,answers);
		if (fragment != null) {
			fragments.put(position, fragment);
		}
		return fragment;
	}

	@Override
	public int getCount() {
		if(answers==null){
			size =1;
		}else {
			size = answers.size();
		}
		return size;
	}
}