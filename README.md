

# Pendo Android app bades on TMDB's api

## Info related to me:
**Name**  *Dor Bismuth*

**email**  *dor.bismut@gmail.com*


## Info related to the app:
*This is an native Android app written in Java.*

*App built using Android Studio build 2020.3.1 and java version 1.8.0_301*

## Notes:

**Screen size compatibility**  *I made a couple tweaks on the UI which may display differently across*

*variety of other android devices and emulators ( I might be wrong tho ) and Iev tested it successfully on my*

*smasung A50 running Android 11 , and on my emulator Pixel 2 API 30*


**Actionbar**  *MovieActivity extends  YouTubeBaseActivity which doesnot seem to have an action bar,*

*but I did made a practice using it on MainActivity where it shows which selection mode is selected.*

**Why I added an youtube view**  *I thought that it might be a cool extra , even if not really requested besides*

*the fact that AppCompatActivity cannot be extended and action bar is not part of it I think it cant make*

*any worse since I have used action bar on the MainActivity and also added some sort of actionbar to MovieActivity.*

### Libraries that I used and why:
**okhttp**  *used to retrieve youutbe's video link from the raw api*

**YouTubeAndroidPlayerApi.jar**  *This libary is needed inorder to have a youtubeview.*



**retrofit**  *My rest client for handling the API calls.*


**picasso**  *Libary which I used to load images from a url to a view.*


**com.google.android.material**  *Needed for better UI experience*


**androidx.recyclerview**  *Better when fetching large sets of data also minimizing memory usage.*




### Classed and methods :
**MovieListAdapter**  *The adapter for the RecyclerView, including the **ViewHolder** which is also my **MVVM** ,*

*based on com.example.pendotmdb.objects.movieObj;*

**apiHandler**  *API handler class making the calls to the service , having two main methods,*
*getMovie by id returning movie object , getMovieList


**apiInterface**  *Interface for the API calls methods.*

### Sources that I used :
*TMDB documantion*

*Github*

*Stackoverflow*

*Android Arenal*


<div align="center">

## App Preview
![Screenshot_2](Screenshot_1.png "Screenshot_2")

![Screenshot_2](Screenshot_1.png "Screenshot_2")


![app gif](app.gif "app gif")