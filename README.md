# Project-1-Movies
Lines 20-22 of the app level build.gradle file has a spot to insert your API Key

  buildTypes.each {
        it.buildConfigField 'String', 'MOVIE_KEY', "\"INSERT API KEY HERE\""
    }
    
Mahalo!
