## Prerequisites

* install ```gradle``` if not present

```
        https://gradle.org
```

### Running the Tests: 

* navigate to the project root directory and run this command while specifying the `testSuite` file: 
``` 
        gradle clean test -PtestSuiteFile=testng-smoke.xml
```
NOTE: You can see detailed test reports at: ```${projectDir}/build/tesngOutput```

=========================================================
