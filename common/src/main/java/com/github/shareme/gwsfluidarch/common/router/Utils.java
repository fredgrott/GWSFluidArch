/*
  The MIT License (MIT)

Copyright (c) 2015 Trikita
Modifications Copyright(C) 2016 Fred Grott(GrottWorkShop)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */
package com.github.shareme.gwsfluidarch.common.router;

import java.util.Map;

/**
 * Created by fgrott on 9/11/2016.
 */
@SuppressWarnings("unused")
public class Utils {

  public static boolean matchUri(String uri, String route, Map<String, String> properties) {
    if (route.length() == 0 || uri.length() == 0) {
      return false;
    }

    int lastRouteIndex = route.indexOf("/");
    int lastUriIndex = uri.indexOf("/");

    // detects uris that do not contain '/'
    if (lastRouteIndex != 0 || lastUriIndex != 0) {
      return false;
    }

    // route consists of only one character, simple compare it with the uri
    if (route.length() <= lastRouteIndex+1) {
      return route.equals(uri);
    }

    // uri consists of only one character, simple compare it with the route
    if (uri.length() <= lastUriIndex+1) {
      return route.equals(uri);
    }

    int newRouteIndex, newUriIndex;
    boolean routeEnded = false;
    boolean uriEnded = false;
    while (true) {
      newRouteIndex = route.indexOf("/", lastRouteIndex+1);
      newUriIndex = uri.indexOf("/", lastUriIndex+1);

      if (newRouteIndex == -1) {
        newRouteIndex = route.length();
        routeEnded = true;
      }

      if (newUriIndex == -1) {
        newUriIndex = uri.length();
        uriEnded = true;
      }

      String routeToken = route.substring(lastRouteIndex+1, newRouteIndex);
      String token = uri.substring(lastUriIndex+1, newUriIndex);

      // copy uri params into the properties
      if (routeToken.startsWith(":")) {
        properties.put(routeToken.substring(1), token);
        // copy uri remainder for parsing by nested routers into the properties
      } else if (routeToken.equals("...")) {
        properties.put("uriRemainder", uri.substring(lastUriIndex));
        return true;
        // mismatch found, go to the next route
      } else if (!routeToken.equals(token)) {
        return false;
      }

      if ((routeEnded && !uriEnded) || (uriEnded && !routeEnded)) {
        return false;
      }

      if (uriEnded && routeEnded) {
        break;
      }

      lastRouteIndex = newRouteIndex;
      lastUriIndex = newUriIndex;
    }

    return true;
  }

}
