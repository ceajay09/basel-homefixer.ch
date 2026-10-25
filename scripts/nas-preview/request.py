#!/usr/bin/env python3
"""Serve one allowlisted GET/HEAD from the private preview API over SSH stdout."""
import base64, json, sys, urllib.request, urllib.error
from urllib.parse import urlsplit

def main():
    request = json.loads(base64.b64decode(sys.argv[1], validate=True))
    path = request['path']
    if request['method'] not in ('GET', 'HEAD') or len(path) > 4096:
        raise ValueError('Unsupported request')
    parsed = urlsplit(path)
    if parsed.scheme or parsed.netloc or not (parsed.path.startswith(('/api/projects', '/api/blog', '/api/media/', '/api/content/')) or parsed.path == '/actuator/health'):
        raise ValueError('Unsupported path')
    if any(ord(char) < 32 for char in path):
        raise ValueError('Invalid path')
    query = urllib.request.Request('http://127.0.0.1:28081' + path, method=request['method'])
    try:
        response = urllib.request.urlopen(query, timeout=20)
    except urllib.error.HTTPError as error:
        response = error
    headers = {key: value for key, value in response.headers.items() if key.lower() in ('content-type', 'cache-control', 'x-content-type-options')}
    metadata = json.dumps({'status': response.code, 'headers': headers})
    sys.stdout.buffer.write(metadata.encode('utf-8') + bytes([10]))
    total = 0
    with response:
        while True:
            chunk = response.read(65536)
            if not chunk:
                break
            total += len(chunk)
            if total > 16 * 1024 * 1024:
                raise ValueError('Response too large')
            sys.stdout.buffer.write(chunk)
    sys.stdout.buffer.flush()

if __name__ == '__main__':
    try:
        main()
    except Exception:
        print('Private preview request failed', file=sys.stderr)
        sys.exit(1)
