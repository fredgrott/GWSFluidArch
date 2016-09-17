GWSSFluidArch
=============

A multipurpose Android Application Architecture Framework for developing 
Android Applications using App Patterns such as MVP, MVVM, and maybe 
even Flux.

# SENIOR ANDROID DEVELOPER NOTE

Android Application Architecture patterns are somewhat different on the 
Android Platform compared to other platforms as one has to balance the 
needs of the lifecycle of Activities, Fragments, Views, etc with the goal 
of having the least amount of things directly coupled.

By implementing View-State and View-Model I can get a cleaner View-Logic Api 
and a cleaner Business-Logic Api and helps reduce the boilerplate required 
to handle device orientation changes. Plus, its easier on developers 
understanding of an application architecture if the view-logic and business-logic 
apis are in their own distinct classes.

The other application architecture aspect is that to produce fluid UI app 
responsive apps one has to off-load expensive tasks off of the UI-thread.
Such tasks are data adaptation for the RecyclerView adapter, network operations 
of loading data, and many others such as editing shared preferences. You 
emphatically do not have to use RxJava or Agera to get an Android 
responsive app.

GWSFluidArch MVP, MVVM, and other App Patterns are designed and developed 
with this in mind.