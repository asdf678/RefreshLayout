package example.refreshlayout.util;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class Utils {
    public static <T extends Fragment> String makeFragmentTag(Class<T> fragmentClazz) {
        return fragmentClazz.getName();
    }

    public static <T extends Fragment> String makeFragmentTag(Fragment fragment) {
        return makeFragmentTag(fragment.getClass());
    }


    public static Fragment addFragment(FragmentManager m, Class<? extends Fragment> clz, int containerId) {
        return addFragment(m, clz, containerId, false, null);
    }

    public static Fragment addFragment(FragmentManager m, Class<? extends Fragment> clz, int containerId, String stackName) {
        return addFragment(m, clz, containerId, true, stackName);
    }


    public static Fragment addFragment(FragmentManager m, Class<? extends Fragment> clz, int containerId, boolean addToBackStack, String stackName) {
        return addFragment(m, clz, containerId, addToBackStack, stackName, null);
    }

    public static Fragment addFragment(FragmentManager m, Class<? extends Fragment> clz, int containerId, boolean addToBackStack, String stackName, Bundle args) {
        Fragment f = null;
        try {
            final String tag = clz.getName();
            f = m.findFragmentByTag(tag);
            if (f == null) {
                f = clz.newInstance();
                f.setArguments(args);
                FragmentTransaction ft = m.beginTransaction().add(containerId, f, tag);
                if (addToBackStack) {
                    ft.addToBackStack(stackName);
                }
                ft.commit();
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return f;

    }

    public static Fragment addFragmentAllowingStateLoss(FragmentManager m, Class<? extends Fragment> clz, int containerId) {
        return addFragmentAllowingStateLoss(m, clz, containerId, false, null);
    }

    public static Fragment addFragmentAllowingStateLoss(FragmentManager m, Class<? extends Fragment> clz, int containerId, String stackName) {
        return addFragmentAllowingStateLoss(m, clz, containerId, true, stackName);
    }


    public static Fragment addFragmentAllowingStateLoss(FragmentManager m, Class<? extends Fragment> clz, int containerId, boolean addToBackStack, String stackName) {
        return addFragmentAllowingStateLoss(m, clz, containerId, addToBackStack, stackName, null);
    }

    public static Fragment addFragmentAllowingStateLoss(FragmentManager m, Class<? extends Fragment> clz, int containerId, boolean addToBackStack, String stackName, Bundle args) {
        Fragment f = null;
        try {
            final String tag = clz.getName();
            f = m.findFragmentByTag(tag);
            if (f == null) {
                f = clz.newInstance();
                f.setArguments(args);
                FragmentTransaction ft = m.beginTransaction().add(containerId, f, tag);
                if (addToBackStack) {
                    ft.addToBackStack(stackName);
                }
                ft.commitAllowingStateLoss();
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return f;

    }




    public static Fragment replaceFragment(FragmentManager m, Class<? extends Fragment> clz,
                                           int containerId) {
        return replaceFragment(m, clz, containerId, false, null);
    }

    public static Fragment replaceFragment(FragmentManager m, Class<? extends Fragment> clz,
                                           int containerId, String stackName) {
        return replaceFragment(m, clz, containerId, true, stackName);
    }


    public static Fragment replaceFragment(FragmentManager m, Class<? extends Fragment> clz,
                                           int containerId, boolean addToBackStack, String stackName) {
        return replaceFragment(m, clz, containerId, addToBackStack, stackName, null);
    }

    public static Fragment replaceFragment(FragmentManager m, Class<? extends Fragment> clz,
                                           int containerId, boolean addToBackStack, String stackName, Bundle args) {
        Fragment f = null;
        try {
            final String tag = clz.getName();
            f = m.findFragmentByTag(tag);
            if (f == null) {
                f = clz.newInstance();
                f.setArguments(args);
                FragmentTransaction ft = m.beginTransaction().replace(containerId, f, tag);
                if (addToBackStack) {
                    ft.addToBackStack(stackName);
                }
                ft.commit();
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return f;
    }

    public static Fragment replaceFragmentAllowingStateLoss(FragmentManager m, Class<? extends Fragment> clz,
                                                            int containerId) {
        return replaceFragmentAllowingStateLoss(m, clz, containerId, false, null);
    }

    public static Fragment replaceFragmentAllowingStateLoss(FragmentManager m, Class<? extends Fragment> clz,
                                                            int containerId, String stackName) {
        return replaceFragmentAllowingStateLoss(m, clz, containerId, true, stackName);
    }


    public static Fragment replaceFragmentAllowingStateLoss(FragmentManager m, Class<? extends Fragment> clz,
                                                            int containerId, boolean addToBackStack, String stackName) {
        return replaceFragmentAllowingStateLoss(m, clz, containerId, addToBackStack, stackName, null);
    }

    public static Fragment replaceFragmentAllowingStateLoss(FragmentManager m, Class<? extends Fragment> clz,
                                                            int containerId, boolean addToBackStack, String stackName, Bundle args) {
        Fragment f = null;
        try {
            final String tag = clz.getName();
            f = m.findFragmentByTag(tag);
            if (f == null) {
                f = clz.newInstance();
                f.setArguments(args);
                FragmentTransaction ft = m.beginTransaction().replace(containerId, f, tag);
                if (addToBackStack) {
                    ft.addToBackStack(stackName);
                }
                ft.commitAllowingStateLoss();
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return f;
    }

}
