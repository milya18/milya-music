import yt_dlp

def get_audio_info(youtube_url):
    ydl_opts = {
        # Force format 251 (best native WebM Opus stream)
        'format': '251/bestaudio/best',
        'quiet': True,
        'no_warnings': True,
        'simulate': True,
        # EXTRACTOR ARGS: This tells YouTube you are a mobile browser/iOS app,
        # which heavily decreases server-side stream bandwidth limits.
        'extractor_args': {
            'youtube': {
                'player_client': ['android', 'ios', 'web'],
                'skip': ['dash', 'hls']
            }
        }
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info = ydl.extract_info(youtube_url, download=False)

        raw_headers = info.get("http_headers", {})
        clean_headers = {str(k): str(v) for k, v in raw_headers.items()}

        return {
            "stream_url": str(info.get("url", "")),
            "title": str(info.get("title", "Unknown Title")),
            "duration": int(info.get("duration", 0)),
            "thumbnail": str(info.get("thumbnail", "")),
            "http_headers": clean_headers
        }

    return None