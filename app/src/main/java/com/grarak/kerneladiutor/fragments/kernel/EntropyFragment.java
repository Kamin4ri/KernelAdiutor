/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutor.fragments.kernel;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.BaseControlFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.entropy.Entropy;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 29.06.16.
 */
public class EntropyFragment extends BaseControlFragment {

    private DescriptionView mAvailable;
    private DescriptionView mPoolSize;

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(ApplyOnBootFragment.ENTROPY));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        int ps = Entropy.getPoolsize();

        mAvailable = new DescriptionView();
        mAvailable.setTitle(getString(R.string.available));
        mAvailable.setSummary(getAvailableDescription(Entropy.getAvailable(), ps));

        items.add(mAvailable);

        mPoolSize = new DescriptionView();
        mPoolSize.setTitle(getString(R.string.poolsize));
        mPoolSize.setSummary(String.valueOf(ps));

        items.add(mPoolSize);

        List<String> list = new ArrayList<>();
        for (int i = 64; i < ps; i *= 2) {
            list.add(String.valueOf(i));
        }

        SeekBarView read = new SeekBarView();
        read.setTitle(getString(R.string.read));
        read.setItems(list);
        read.setProgress(list.indexOf(String.valueOf(Entropy.getRead())));
        read.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Entropy.setRead(Utils.strToInt(value), getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(read);

        SeekBarView write = new SeekBarView();
        write.setTitle(getString(R.string.write));
        write.setItems(list);
        write.setProgress(list.indexOf(String.valueOf(Entropy.getWrite())));
        write.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Entropy.setWrite(Utils.strToInt(value), getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(write);
    }

    private String getAvailableDescription(int available, int poolsize) {
        return Utils.roundTo2Decimals((double) available * 100 / (double) poolsize) + "% (" + available + ")";
    }

    @Override
    protected void refresh() {
        super.refresh();

        int ps = Entropy.getPoolsize();
        if (mAvailable != null) {
            mAvailable.setSummary(getAvailableDescription(Entropy.getAvailable(), ps));
        }
        if (mPoolSize != null) {
            mPoolSize.setSummary(String.valueOf(ps));
        }
    }
}
