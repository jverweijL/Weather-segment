Based on the weather you can show different content.

Make sure to configure the following property in portal-ext.properties
```
segment.openweathermap.apikey=
```

The module will lookup the ipnumber from the client but if you are running on localhost (dev)
you might want to hard-wire the ip. In this case you can set the following property in portal-ext.properties
```
segment.weather.myip
```

Some examples for segmenting based on weather

- show a skirt when it's sunny vs. a rainjacket when it's rainy
- show a convertible when it's sunny vs. a 4x4 when it's snowy