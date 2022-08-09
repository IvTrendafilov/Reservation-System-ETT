## Installation guide
<ul>
<li>Download latest PostgreSQL version with a graphical interface (like pgAdmin)</li>
</ul>

I will explain how to setup the DB in pgAdmin (it may differ in Intellij database if you want to use it, but the steps taken should be the same)

<ol>
<li>
Open pgAdmin, right click PostgreSQL 13 -> Create -> Login/Group Role <br />
In the General tab under Name type "group18" (without quotes) <br />
In the Definition tab under Password type "group18" (without quotes) <br />
In the Privileges tab toggle ON Can login?, Create databases?, Inherit rights from parent roles? <br />
Now you can click on create!
</li>
<li>
Right click on Databases -> Create Database <br />
Under Database type "eetreservationsystem" (without quotes) and under Owner pick group18 <br/>
</li>
<li>
Get in intellij, top right corner click on gradle -> reservationsystem -> Tasks -> application -> bootRun <br/>
From now on you can start it top run button, as it has set it as default run option
</li>
<li>
In order to get to the main page go to your browser and navigate to http://localhost:8081/pages/person <br/>
Then you can look at the code and ask me if there is anything weird in there
</li>
<li>
Important thing!!! <br/>
If you want to update the database, you should go to /resources/db.migrations/ and create a sql file with this pattern <br/>
VX.X.X__name_of_file.sql <br/>
Then when you run the project, it will automatically update the database according to the sql script in it. This allows us to have smooth migrations without any errors!!
</li>
</ol>