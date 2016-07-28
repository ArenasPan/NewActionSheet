# NewActionSheet
this is a Android style UIActionSheet

We can use it like this,

	NewActionSheet as = new NewActionSheet(this);
    as.addItems(ITEMS);
    as.setItemClickListener(this);
    as.setCancelableOnTouchMenuOutside(true);
    as.showMenu();

