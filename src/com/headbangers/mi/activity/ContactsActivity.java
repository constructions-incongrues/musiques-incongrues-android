package com.headbangers.mi.activity;

import roboguice.activity.GuiceListActivity;
import roboguice.inject.InjectResource;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.headbangers.mi.R;

public class ContactsActivity extends GuiceListActivity {

    @InjectResource(R.array.external_links)
    protected String[] links;

    protected static int[] icons = { R.drawable.wtf, R.drawable.web,
            R.drawable.mail, R.drawable.telephone, R.drawable.map };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        setListAdapter(new ContactsAdapter(this));
    }

    public class ContactsAdapter extends ArrayAdapter<String> {

        private ContactsActivity context;

        public ContactsAdapter(ContactsActivity context) {
            super(context, R.layout.one_link, links);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                row = inflater.inflate(R.layout.one_link, null);
            }

            TextView label = (TextView) row.findViewById(R.id.menuLabel);
            TextView description = (TextView) row
                    .findViewById(R.id.menuDisclaimer);
            ImageView icon = (ImageView) row.findViewById(R.id.menuIcon);
            TextView link = (TextView) row.findViewById(R.id.menuLink);

            String[] linkDefinition = links[position].split("_");

            label.setText(linkDefinition[0]);
            description.setText(linkDefinition[1]);
            link.setText(linkDefinition[2]);

            int zeIcon = 0;
            try {
                zeIcon = Integer.parseInt(linkDefinition[3]);
            } catch (NumberFormatException e) {
                zeIcon = 0;
            }
            Linkify.addLinks(link, Linkify.ALL);

            icon.setImageResource(icons[zeIcon]);

            return row;
        }
    }

}
