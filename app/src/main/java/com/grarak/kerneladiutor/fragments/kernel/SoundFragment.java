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
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.kernel.sound.Sound;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;

import java.util.List;

/**
 * Created by willi on 26.06.16.
 */
public class SoundFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        if (Sound.hasSoundControlEnable()) {
            soundControlEnableInit(items);
        }
        if (Sound.hasHighPerfModeEnable()) {
            highPerfModeEnableInit(items);
        }
        if (Sound.hasHeadphoneGain()) {
            headphoneGainInit(items);
        }
        if (Sound.hasHeadphonePowerAmpGain()) {
            headphonePowerAmpGainInit(items);
        }
        if (Sound.hasSpeakerGain()) {
            speakerGainInit(items);
        }
        if (Sound.hasHandsetMicrophoneGain()) {
            handsetMicrophoneGainInit(items);
        }
        if (Sound.hasCamMicrophoneGain()) {
            camMicrophoneGainInit(items);
        }
        if (Sound.hasHeadphoneTpaGain()) {
            headphoneTpaGainInit(items);
        }
        if (Sound.hasLockOutputGain()) {
            lockOutputGainInit(items);
        }
        if (Sound.hasLockMicGain()) {
            lockMicGainInit(items);
        }
        if (Sound.hasMicrophoneGain()) {
            microphoneGainInit(items);
        }
        if (Sound.hasVolumeGain()) {
            volumeGainInit(items);
        }
    }

    private void soundControlEnableInit(List<RecyclerViewItem> items) {
        SwitchView soundControl = new SwitchView();
        soundControl.setSummary(getString(R.string.sound_control));
        soundControl.setChecked(Sound.isSoundControlEnabled());
        soundControl.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Sound.enableSoundControl(isChecked, getActivity());
            }
        });

        items.add(soundControl);
    }

    private void highPerfModeEnableInit(List<RecyclerViewItem> items) {
        SwitchView highPerfMode = new SwitchView();
        highPerfMode.setSummary(getString(R.string.headset_highperf_mode));
        highPerfMode.setChecked(Sound.isHighPerfModeEnabled());
        highPerfMode.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Sound.enableHighPerfMode(isChecked, getActivity());
            }
        });

        items.add(highPerfMode);
    }

    private void headphoneGainInit(List<RecyclerViewItem> items) {
        final CardView headphoneGain = new CardView(getActivity());
        headphoneGain.setTitle(getString(R.string.headphone_gain));

        // Set this bool to false if it doesn't exist
        if (!(Prefs.getBoolean("fauxsound_perchannel_hp_gain", false, getActivity()))) {
        	Prefs.saveBoolean("fauxsound_perchannel_hp_gain", false, getActivity());
        }

        // Define the switch first
        final SwitchView perChannelControls = new SwitchView();
        perChannelControls.setTitle(getString(R.string.per_channel_controls));
        perChannelControls.setSummary(getString(R.string.per_channel_controls_summary));
        perChannelControls.setChecked(Prefs.getBoolean("fauxsound_perchannel_hp_gain", false, getActivity()));
        headphoneGain.addItem(perChannelControls);

        // This seekbar controls gain either for all channels or for left channel only.
        // This is changed via Refresh.refresh().
        final SeekBarView gain1 = new SeekBarView();
        headphoneGain.addItem(gain1);

        final SeekBarView gain2 = new SeekBarView();
        gain2.setTitle(getString(R.string.right_channel));
        gain2.setItems(Sound.getHeadphoneGainLimits());
        gain2.setProgress(Sound.getHeadphoneGainLimits().indexOf(Sound.getHeadphoneGain("right")));
        gain2.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphoneGain("right", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        class Refresh {
            public void refresh (String type) {

                switch (type) {
                    case "perchannel":
                        gain1.setTitle(getString(R.string.left_channel));
                        gain1.setItems(Sound.getHeadphoneGainLimits());
                        gain1.setProgress(Sound.getHeadphoneGainLimits().indexOf(Sound.getHeadphoneGain("left")));
                        gain1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                            @Override
                            public void onStop(SeekBarView seekBarView, int position, String value) {
                                Sound.setHeadphoneGain("left", value, getActivity());
                            }

                            @Override
                            public void onMove(SeekBarView seekBarView, int position, String value) {
                            }
                        });
                        headphoneGain.addItem(gain2);
                        break;
                    case "single":
                        gain1.setTitle(getString(R.string.all_channels));
                        gain1.setItems(Sound.getHeadphoneGainLimits());
                        gain1.setProgress(Sound.getHeadphoneGainLimits().indexOf(Sound.getHeadphoneGain("all")));
                        gain1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                            @Override
                            public void onStop(SeekBarView seekBarView, int position, String value) {
                                Sound.setHeadphoneGain("all", value, getActivity());
                            }

                            @Override
                            public void onMove(SeekBarView seekBarView, int position, String value) {
                            }
                        });
                        gain2.setProgress(Sound.getHeadphoneGainLimits().indexOf(Sound.getHeadphoneGain("all")));
                        try {
                            headphoneGain.removeItem(gain2);
                        } catch (Throwable t) {
                            // Don't do anything
                        }
                        break;
                }
            }
        }

        if (Prefs.getBoolean("fauxsound_perchannel_hp_gain", true, getActivity())) {
            new Refresh().refresh("perchannel");
        } else {
            new Refresh().refresh("single");
        }  

        perChannelControls.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Prefs.saveBoolean("fauxsound_perchannel_hp_gain", isChecked, getActivity());
                if (isChecked == true) {
                    new Refresh().refresh("perchannel");
                } else {
                    new Refresh().refresh("single");
                    Sound.setHeadphoneGain("all", Sound.getHeadphoneGain("left"), getActivity());
                }
            }
        });

        items.add(headphoneGain);
    }

    private void handsetMicrophoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView handsetMicrophoneGain = new SeekBarView();
        handsetMicrophoneGain.setTitle(getString(R.string.handset_microphone_gain));
        handsetMicrophoneGain.setItems(Sound.getHandsetMicrophoneGainLimits());
        handsetMicrophoneGain.setProgress(Sound.getHandsetMicrophoneGainLimits()
                .indexOf(Sound.getHandsetMicrophoneGain()));
        handsetMicrophoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHandsetMicrophoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(handsetMicrophoneGain);
    }

    private void camMicrophoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView camMicrophoneGain = new SeekBarView();
        camMicrophoneGain.setTitle(getString(R.string.cam_microphone_gain));
        camMicrophoneGain.setItems(Sound.getCamMicrophoneGainLimits());
        camMicrophoneGain.setProgress(Sound.getCamMicrophoneGainLimits().indexOf(Sound.getCamMicrophoneGain()));
        camMicrophoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setCamMicrophoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(camMicrophoneGain);
    }

    private void speakerGainInit(List<RecyclerViewItem> items) {
        final CardView speakerGain = new CardView(getActivity());
        speakerGain.setTitle(getString(R.string.speaker_gain));

        final SwitchView perChannelControls = new SwitchView();
        perChannelControls.setTitle(getString(R.string.per_channel_controls));
        perChannelControls.setSummary(getString(R.string.per_channel_controls_summary));
        perChannelControls.setChecked(Prefs.getBoolean("fauxsound_perchannel_sp_gain", false, getActivity()));
        speakerGain.addItem(perChannelControls);

        // Set this to false if it doesn't exist
        if (!(Prefs.getBoolean("fauxsound_perchannel_sp_gain", false, getActivity()))) {
            Prefs.saveBoolean("fauxsound_perchannel_sp_gain", false, getActivity());
        }

        // This seekbar controls gain either for all channels or for left channel only.
        // This is changed via Refresh.refresh().
        final SeekBarView gain1 = new SeekBarView();
        speakerGain.addItem(gain1);

        final SeekBarView gain2 = new SeekBarView();
        gain2.setTitle(getString(R.string.right_channel));
        gain2.setItems(Sound.getSpeakerGainLimits());
        gain2.setProgress(Sound.getSpeakerGainLimits().indexOf(Sound.getSpeakerGain("right")));
        gain2.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setSpeakerGain("right", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        class Refresh {
            public void refresh (String type) {
                switch (type) {
                    case "perchannel":
                        gain1.setTitle(getString(R.string.left_channel));
                        gain1.setItems(Sound.getSpeakerGainLimits());
                        gain1.setProgress(Sound.getSpeakerGainLimits().indexOf(Sound.getSpeakerGain("left")));
                        gain1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                            @Override
                            public void onStop(SeekBarView seekBarView, int position, String value) {
                                Sound.setSpeakerGain("left", value, getActivity());
                            }

                            @Override
                            public void onMove(SeekBarView seekBarView, int position, String value) {
                            }
                        });
                        speakerGain.addItem(gain2);
                        break;
                    case "single":
                        gain1.setTitle(getString(R.string.all_channels));
                        gain1.setItems(Sound.getSpeakerGainLimits());
                        gain1.setProgress(Sound.getSpeakerGainLimits().indexOf(Sound.getSpeakerGain("all")));
                        gain1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                            @Override
                            public void onStop(SeekBarView seekBarView, int position, String value) {
                                Sound.setSpeakerGain("all", value, getActivity());
                            }

                            @Override
                            public void onMove(SeekBarView seekBarView, int position, String value) {
                            }
                        });
                        gain2.setProgress(Sound.getSpeakerGainLimits().indexOf(Sound.getSpeakerGain("all")));
                        try {
                            speakerGain.removeItem(gain2);
                        } catch (Throwable t) {
                            // Don't do anything
                        }
                        break;
                }
            }
        }

        if (Prefs.getBoolean("fauxsound_perchannel_sp_gain", true, getActivity())) {
            new Refresh().refresh("perchannel");
        } else {
            new Refresh().refresh("single");
        }  

        perChannelControls.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Prefs.saveBoolean("fauxsound_perchannel_sp_gain", isChecked, getActivity());
                if (isChecked == true) {
                    new Refresh().refresh("perchannel");
                } else {
                    new Refresh().refresh("single");
                    Sound.setSpeakerGain("all", Sound.getHeadphoneGain("left"), getActivity());
                }
            }
        });

        items.add(speakerGain);
    }

    private void headphonePowerAmpGainInit(List<RecyclerViewItem> items) {
        final CardView headphonePAGain = new CardView(getActivity());
        headphonePAGain.setTitle(getString(R.string.headphone_poweramp_gain));

        final SwitchView perChannelControls = new SwitchView();
        perChannelControls.setTitle(getString(R.string.per_channel_controls));
        perChannelControls.setSummary(getString(R.string.per_channel_controls_summary));
        perChannelControls.setChecked(Prefs.getBoolean("fauxsound_perchannel_hp_pa_gain", false, getActivity()));
        headphonePAGain.addItem(perChannelControls);

        // Set this to false if it doesn't exist
        if (!(Prefs.getBoolean("fauxsound_perchannel_hp_pa_gain", false, getActivity()))) {
            Prefs.saveBoolean("fauxsound_perchannel_hp_pa_gain", false, getActivity());
        }

        // This seekbar controls gain either for all channels or for left channel only.
        // This is changed via Refresh.refresh().
        final SeekBarView gain1 = new SeekBarView();
        headphonePAGain.addItem(gain1);

        final SeekBarView gain2 = new SeekBarView();
        gain2.setTitle(getString(R.string.right_channel));
        gain2.setItems(Sound.getHeadphonePowerAmpGainLimits());
        gain2.setProgress(Sound.getHeadphonePowerAmpGainLimits().indexOf(Sound.getHeadphonePowerAmpGain("right")));
        gain2.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphonePowerAmpGain("right", value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        class Refresh {
            public void refresh (String type) {
                switch (type) {
                    case "perchannel":
                        gain1.setTitle(getString(R.string.left_channel));
                        gain1.setItems(Sound.getHeadphonePowerAmpGainLimits());
                        gain1.setProgress(Sound.getHeadphonePowerAmpGainLimits().indexOf(Sound.getHeadphonePowerAmpGain("left")));
                        gain1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                            @Override
                            public void onStop(SeekBarView seekBarView, int position, String value) {
                                Sound.setHeadphonePowerAmpGain("left", value, getActivity());
                            }

                            @Override
                            public void onMove(SeekBarView seekBarView, int position, String value) {
                            }
                        });
                        headphonePAGain.addItem(gain2);
                        break;
                    case "single":
                        gain1.setTitle(getString(R.string.all_channels));
                        gain1.setItems(Sound.getHeadphonePowerAmpGainLimits());
                        gain1.setProgress(Sound.getHeadphonePowerAmpGainLimits().indexOf(Sound.getHeadphonePowerAmpGain("all")));
                        gain1.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
                            @Override
                            public void onStop(SeekBarView seekBarView, int position, String value) {
                                Sound.setHeadphonePowerAmpGain("all", value, getActivity());
                            }

                            @Override
                            public void onMove(SeekBarView seekBarView, int position, String value) {
                            }
                        });
                        gain2.setProgress(Sound.getHeadphonePowerAmpGainLimits().indexOf(Sound.getHeadphonePowerAmpGain("all")));
                        try {
                            headphonePAGain.removeItem(gain2);
                        } catch (Throwable t) {
                            // Don't do anything
                        }
                        break;
                }
            }
        }

        if (Prefs.getBoolean("fauxsound_perchannel_hp_pa_gain", true, getActivity())) {
            new Refresh().refresh("perchannel");
        } else {
            new Refresh().refresh("single");
        }  

        perChannelControls.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Prefs.saveBoolean("fauxsound_perchannel_hp_pa_gain", isChecked, getActivity());
                if (isChecked == true) {
                    new Refresh().refresh("perchannel");
                } else {
                    new Refresh().refresh("single");
                    Sound.setHeadphonePowerAmpGain("all", Sound.getHeadphonePowerAmpGain("left"), getActivity());
                }
            }
        });

        items.add(headphonePAGain);
    }

    private void headphoneTpaGainInit(List<RecyclerViewItem> items) {
        SeekBarView headphoneTpaGain = new SeekBarView();
        headphoneTpaGain.setTitle(getString(R.string.headphone_tpa6165_gain));
        headphoneTpaGain.setItems(Sound.getHeadphoneTpaGainLimits());
        headphoneTpaGain.setProgress(Sound.getHeadphoneTpaGainLimits()
                .indexOf(Sound.getHeadphoneTpaGain()));
        headphoneTpaGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setHeadphoneTpaGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(headphoneTpaGain);
    }

    private void lockOutputGainInit(List<RecyclerViewItem> items) {
        SwitchView lockOutputGain = new SwitchView();
        lockOutputGain.setTitle(getString(R.string.lock_output_gain));
        lockOutputGain.setSummary(getString(R.string.lock_output_gain_summary));
        lockOutputGain.setChecked(Sound.isLockOutputGainEnabled());
        lockOutputGain.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Sound.enableLockOutputGain(isChecked, getActivity());
            }
        });

        items.add(lockOutputGain);
    }

    private void lockMicGainInit(List<RecyclerViewItem> items) {
        SwitchView lockMicGain = new SwitchView();
        lockMicGain.setTitle(getString(R.string.lock_mic_gain));
        lockMicGain.setSummary(getString(R.string.lock_mic_gain_summary));
        lockMicGain.setChecked(Sound.isLockMicGainEnabled());
        lockMicGain.addOnSwitchListener(new SwitchView.OnSwitchListener() {
            @Override
            public void onChanged(SwitchView switchView, boolean isChecked) {
                Sound.enableLockMicGain(isChecked, getActivity());
            }
        });

        items.add(lockMicGain);
    }

    private void microphoneGainInit(List<RecyclerViewItem> items) {
        SeekBarView microphoneGain = new SeekBarView();
        microphoneGain.setTitle(getString(R.string.microphone_gain));
        microphoneGain.setItems(Sound.getMicrophoneGainLimits());
        microphoneGain.setProgress(Sound.getMicrophoneGainLimits().indexOf(Sound.getMicrophoneGain()));
        microphoneGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setMicrophoneGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(microphoneGain);
    }

    private void volumeGainInit(List<RecyclerViewItem> items) {
        SeekBarView volumeGain = new SeekBarView();
        volumeGain.setTitle(getString(R.string.volume_gain));
        volumeGain.setItems(Sound.getVolumeGainLimits());
        volumeGain.setProgress(Sound.getVolumeGainLimits().indexOf(Sound.getVolumeGain()));
        volumeGain.setOnSeekBarListener(new SeekBarView.OnSeekBarListener() {
            @Override
            public void onStop(SeekBarView seekBarView, int position, String value) {
                Sound.setVolumeGain(value, getActivity());
            }

            @Override
            public void onMove(SeekBarView seekBarView, int position, String value) {
            }
        });

        items.add(volumeGain);
    }

}
