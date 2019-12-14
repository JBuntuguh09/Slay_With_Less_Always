package com.lonewolf.slay_with_less_always.Resources;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.lonewolf.slay_with_less_always.Fragments.Contact;
import com.lonewolf.slay_with_less_always.Fragments.Upcoming;
import com.lonewolf.slay_with_less_always.Fragments.View_In_Stock;

public class PageAdapterMain extends FragmentStatePagerAdapter {
    public PageAdapterMain(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                return new Upcoming();
            case 1 :
                return new View_In_Stock();
            case 2 :
                return new Contact();
            default:
                return null;

        }


    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 :
                return "Featured";
            case 1 :
                return "In Stock";
            case 2 :
                return "Contact Us";

             default:
                 return null;


        }
       // return super.getPageTitle(position);
    }

}
