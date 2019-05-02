# uFree

An application for getting out and having fun!

Made by: Christian, Tianyue, Alice, Minqi (Team D)

uFree is an app to find friends who are available at the same times you are when you want to go out! The app provides options for users looking to spontaneously meet up with friends as well as for users interested in making plans in advance.

## Test accounts
- Email: jackufree@ufree.com
- Password: 123456
- Email: maryufree@ufree.com
- Password: 123456

## First Sprint Objectives:
- Log In 
  - Create LogIn activity.
  - Create "Log in" and "Sign up" buttons. The user can navigate to either the Sign Up page if they do not already have an account with us, or they can fill in the email and password fields and enter into the app.
  - Make email and password required input fields, with the email having to contain at least one @ symbol, and the password field having length 6 or more. We used Firebase to authenticate the users.
  - If either the email or the password is incorrect, a Toast box will appear on the Page, disallowing the user to continue into the app.

- Sign Up
  - Create Sign Up activity, which can be navigated to from the sign up page.
  - Require new users to provide us with their full name, phone number, email, and password.
  - If an email is  already in our database, the user would not be allowed to register and a Toast box will tell them accordingly.
  - If all the fields are completed, the user can press the sign up button, where they will be registered as an authenticated user, and their information will be propagated into our database, and proceed to the Welcome Activity.

- Welcome
  - Create Welcome activity.
  - Create "Yes" and "No" button, and the user can select if he/she is free.
  - If "Yes" is selectd, add pop up window to ask for how long they are free. Add slider bar to read user input.
  - If "No" is selected, direct the user to Main activity.
  - Write user response to fireabse correspondingly.

- Who is free
  - Create Main Activity.
  - Implement recycler view to display free users in the firebase.
  - Implement time machine at the bottom. User can select time, and the list of available freinds change correspondingly.

- Events
  - Create Event Activity.
  - Add recycler view to see a list of events.
  - Can click on single item to see event detail.
  - Add floating button to create a new event.

- Profile
  - Implement function of changing name, phone number, password, and deleting account.
  - Users can click the edit button on their profile page to edit their user names as well as phone numbers. 
  - Users can click the “change password” tab to request emails with links to reset their passwords.- Users can delete their accounts at the bottom of the screen. A confirmation message is set up to prevent unintended deletion of account.

- Navigation drawer
  - Add 5 main navigation menus 
  - Add time button and toggle, so the user can change his/her free status.

## Second Sprint Objectives:
- Sign up 
  - Implemented upload of profile picture in Sign Up from user's gallery

- Who is free
  - Display free friends instead of all free users in the database
  - Order friends based on free time
  - Implement mutiple selection to create an event with mutiple friends

- Friend
  - Receiving and sending friend request
  - Accepting and rejecting friend request
  - Deleting friends
  - Write search class that provides public functions to search for friends/all users with their names

- Event
  - blah

- Profile
  - Users with profile picture will have their profile pictures displayed in all appropriate places, while users without will retain the default picture.
  - Users can change their profile picture in the Profile page


 
 ## General State of the App
 In our first sprint product, the user can create an account, log in and delete account. They will see all free users in the database, and can use time machine to filter free users based on their availability. They can create an event with a single person, and view events detail on Events page. The user can also revise their name, phone number, or change password in Profile page.

 In our second sprint product, the user can upload a profile picture, change the profile picture. The user can search for freinds, send friend requests, and add/delete freinds. Only free friends are visible on Who is free page. The user can create event, send event invitation and edit events.

 
 ## Meeting Notes:
 
 4/1 Meet with Ryenne. Start making UI for all activities.

 4/6 Work day. Merge all UIs to master.
  
 4/8 Meet with Ryenne. Start with setting up firebase.
 
 4/14 Work day. Sprint before deadline.

 4/21 Plan Sprint 2 goals. Work day.

 4/27 Meet with Ryenne. Prepare for demo.

 5/2 Wrap up on sprint 2. Ready for submission!
