# Finance budget app
An Android application to track your finances 

This application is made using Kotlin and Jetpack Compose for the UI. Gradle is used as the build system.

## Functionality

#### 1. Category creation / editing

A user has the ability to create, edit, delete categories. \
Each category has a name, user defined color and icon.
<p>
  <img src="images/category_1.png" width="20%" height="20%" />
  <img src="images/category_2.png" width="20%" height="20%" />
  <img src="images/category_3.png" width="20%" height="20%" />
</p>

#### 2. Transaction creation / editing

A user can register an expense or income transaction using the + button at the bottom of the screen.

The user will be promted to enter a value, assign a category, write a note (optional) and change the time of the transaction that is set to the system time of the device by default.

<p>
  <img src="images/transaction_1.png" width="20%" height="20%" />
  <img src="images/transaction_2.png" width="20%" height="20%" />
  <img src="images/transaction_3.png" width="20%" height="20%" />
  <img src="images/transactions_2.png" width="20%" height="20%" />
</p>

#### 3. View categories

The user also has the ability to view all the available categories and enter the categories to edit them or delete from the menu. \
(Note: if there is at least 1 transaction assigned to a category deleting it will result in the transaction being assigned to UNKNOWN category).

<p>
  <img src="images/categories_1.png" width="20%" height="20%" />
  <img src="images/categories_2.png" width="20%" height="20%" />
</p>

#### 4. View transactions

The user can view all available transactions that meet filter requirments (filter by what types of categories to show, time range or text content). \
They can also enter transaction edit mode from this screen.

<p>
  <img src="images/transactions_1.png" width="20%" height="20%" />
  <img src="images/transactions_3.png" width="20%" height="20%" />
  <img src="images/transactions_2.png" width="20%" height="20%" />
</p>
<p>
  <img src="images/transactions_4.png" width="20%" height="20%" />
  <img src="images/transactions_5.png" width="20%" height="20%" />
  <img src="images/transactions_6.png" width="20%" height="20%" />
</p>

#### 5. Statistics

Finally the user can view the statistics for each category during a selected time range and other filters.

<img src="images/statistics.png" width="20%" height="20%" />

