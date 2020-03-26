# API returns json with names beginning with numbers. This function renames these fields.
# A required attribute that provides the name for the enum.
# This name must begin with [A-Za-z_], and subsequently contain only [A-Za-z0-9_].


def rename(randomWeather):
    if 'snow' in randomWeather:
        if '1h' in randomWeather['snow']:
            randomWeather['snow']['one_h'] = randomWeather['snow']['1h']
            del randomWeather['snow']['1h']
        if '3h' in randomWeather['snow']:
            randomWeather['snow']['three_h'] = randomWeather['snow']['3h']
            del randomWeather['snow']['3h']

    if 'rain' in randomWeather:
        if '1h' in randomWeather['rain']:
            randomWeather['rain']['one_h'] = randomWeather['rain']['1h']
            del randomWeather['rain']['1h']
        if '3h' in randomWeather['rain']:
            randomWeather['rain']['three_h'] = randomWeather['rain']['3h']
            del randomWeather['rain']['3h']

    return randomWeather
