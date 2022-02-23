[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://forthebadge.com)
[![forthebadge](https://forthebadge.com/images/badges/built-for-android.svg)](https://forthebadge.com)

# Real-Estate-Manager

Real Estate Manager is an android application for real estate agents.

### How it work
 - You can create property that are on sale and save it on the application.
 - Once there is one or multiple items, they are listed in the main screen.
 - You can edit a property and change any fields you want as long as it not sold.
 - Property are geo localized and are marked on the map screen, click on a marker show the details.
 - You can search for property that are save with multiple fields.
 - You can do a loan simulation with an amount(if available), loan rate and duration.
 - You can sell a property, when you do it the property can' be edited.
 - There's an offline mode, if the user doesn't have a connection (mobile or wifi), maps and static map don't work and if the user create a property offline, the property will not have geo localization. User need to edit the property.

### Content provider
 A content provider is available, you can get the property with fileds and photos but you can't create, update or delete property.
 You can get property with these method:
  - contentResolver.query(ContentUris.withAppendedId("content://com.openclassrooms.realestatemanager/RealEstate", ID), null, "property", null, null) //For property
  - contentResolver.query(ContentUris.withAppendedId("content://com.openclassrooms.realestatemanager/RealEstatePhoto", ID), null, "photos", null, null) //For photos
 
 ID is a Long > 0
 The photos corresponding to the property contains the same real_estate_id.
 
 ### Installation
- Get app-release.apk at RealEstateManager\app\release
- Copy and install it on an android smartphone.

### Author
Charles freddy
