#The Moods System

So basically this is a little way for you to separate all your behavior code so that it is trivial to add new behavior, and react dynamically to the environment really easily.

Your unit logic need only have a MoodController.
The class containing the MoodController should have **no** interactions with the moods, **except** for setting the initial mood on the construction of the MoodController.


You are to make a class, extending Mood, for each behavior you want your robot to exhibit. 
You'll pass the initial mood into the MoodController right when your robot is made, then you just call 
run() on your MoodController each round in your robot's loop.
If you want this to work as best as possible, don't put any logic that modifies your robot's state outside of a mood.

## The functions

###update()
This function is the first function to get called each round and is really just a spot for you to update all your class variables. Such as, where this unit is, look around for enemy units, etc.

Mostly just convenience.

###swing()  
This let's you have a mood swing :grimacing: to a different mood.
for instance, if you were in an "Aggressive" mood, and you saw a bunch of enemy units, you could swing to a "Spooked" mood, which has your running away logic.
If you return null, you do not swing.
Also, if you return the same class of mood, you will also not swing.

###transfer(Mood m)
This is called when you are swinging to a new mood. Because update() has already been called, and the *new* mood is the one getting called to act, we might want to transfer over variables that are expensive to calculate. This allows some level of persistance between Moods, but ideally this shouldn't really be used for permanent variables, just ephemeral ones.

###act()
The bread and butter of each mood, this is the function where you want to put all the stuff that modifies your robot's state. 


