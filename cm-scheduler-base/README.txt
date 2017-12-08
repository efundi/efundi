This is an example package that will get you started in creating quartz jobs without modifying any existing Sakai code.

You can drop this entire package into your Sakai source code, build, deploy and go.

There is a sample job, HelloWorld, which will register itself with the JobScheduler and you can run it.

From this you should be able to create your own jobs and register them easier.

Take a look at:
- pack/src/webapp/WEB-INF/components.xml 
	for the registration beans
- scheduler-jobs/src/java/ac/uk/lancs/e_science/jobs/HelloWorld.java
	for the job example

For more information, please see:
http://confluence.sakaiproject.org/confluence/display/ENC/Quartz+in+Sakai

----
Steve Swinsburg
September 2008