[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins%2Fgit-automerger-plugin%2Fmaster)](https://ci.jenkins.io/job/Plugins/job/git-automerger-plugin/job/master/)

The main purpose of this plugin is to ensure that all newer versions include all the changes from older versions.

### Requirements

The plugin is merging only the branches with the following structure:
```
*  master
|
| * release/2.0
|/
|
| * release/1.5
|/
|
| * release/1.0
|/
```

The naming of release branches is flexible, but the branch name must contain a version number.
Versions are ordered using [Apache's ComparableVersion](https://github.com/apache/maven/blob/master/maven-artifact/src/main/java/org/apache/maven/artifact/versioning/ComparableVersion.java).

**Warning:** Do not use the plugin if your version system differs from `ComparableVersion`.
The plugin can merge newer package into older.

If you are using a different branch strategy, please fill a feature request.

### Conflicts

In many cases merge conflicts can be solved automatically.
You could define one of available conflict resolution per file.

<p>For example you have conflict:</p>

<table>
    <tr>
        <th>release/1.0</th>
        <th>release/1.1</th>
    </tr>
    <tr>
        <td>Foo</td>
        <td>Bar</td>
    </tr>
</table>

<p>According to selected resolution, one of outcome could be:</p>

<table>
    <tr>
        <th>KEEP_OLDER</th>
        <th>KEEP_NEWER</th>
        <th>MERGE_NEWER_TOP</th>
        <th>MERGE_OLDER_TOP</th>
    </tr>
    <tr>
        <td>Foo</td>
        <td>Bar</td>
        <td>Bar<br/>Foo</td>
        <td>Foo<br/>Bar</td>
    </tr>
</table>

For example, for `CHANGELOG.md` you would probably prefer to use `MERGE_NEWER_TOP`

### Example:
```groovy
gitAutomerger logLevel: 'INFO',
              mergeRules: [
                  [path: 'CHANGELOG.md', resolution: 'MERGE_NEWER_TOP'],
                  [path: 'version', resolution: 'KEEP_NEWER'],
              ],
              releaseBranchPattern: 'release/%',
              checkoutFromRemote: true,
              remoteName: 'origin',
              detailConflictReport: true
```

### Testing

Run server (http://localhost:8080):
```
./gradlew server
```

### License

```
MIT License

Copyright (c) 2019 Vinted UAB

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
```
