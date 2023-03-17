<img alt="logo" src="http://img.jcabi.com/logo-square.png" width="64px" height="64px" />

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](https://www.rultor.com/b/jcabi/jcabi-github)](https://www.rultor.com/p/jcabi/jcabi-github)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/jcabi/jcabi-github/actions/workflows/mvn.yml/badge.svg)](https://github.com/jcabi/jcabi-github/actions/workflows/mvn.yml)
[![PDD status](https://www.0pdd.com/svg?name=jcabi/jcabi-github)](https://www.0pdd.com/p?name=jcabi/jcabi-github)
[![codecov](https://codecov.io/gh/jcabi/jcabi-github/branch/master/graph/badge.svg)](https://codecov.io/gh/jcabi/jcabi-github)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-github/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-github)
[![JavaDoc](https://img.shields.io/badge/javadoc-html-blue.svg)](https://www.javadoc.io/doc/com.jcabi/jcabi-github)
[![Hits-of-Code](https://hitsofcode.com/github/jcabi/jcabi-github)](https://hitsofcode.com/view/github/jcabi/jcabi-github)
![Lines of code](https://img.shields.io/tokei/lines/github/jcabi/jcabi-github)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/jcabi/jcabi-github/blob/master/LICENSE.txt)

This is a Java adapter to the [GitHub RESTful API](https://developer.github.com/v3/).
There are a few other similar implementations on the market, but jcabi-github has a very strong
focus on object-oriented principles of programming. On top of that,
we have a unique implemenation of GitHub server-side functionality,
which you can use in your unit tests, eliminating the necessity to connect
to GitHub during unit/integration testing.
Please, read the blog post
[_Object-Oriented Github API_](https://www.yegor256.com/2014/05/14/object-oriented-github-java-sdk.html)
by [Yegor Bugayenko](https://www.yegor256.com), the creator of this library.

Java 8 or higher is required.

More details are here: [github.jcabi.com](https://github.jcabi.com/).

You may also get help in this [Telegram chat](https://t.me/elegantobjects).

The set of classes in the
[`com.jcabi.github`](https://static.javadoc.io/com.jcabi/jcabi-github/1.0/com/jcabi/github/package-frame.html)
package is the object-oriented API. Use it like this:

## Work with Github's API

By default, the library works with Github's API (https://api.github.com)

```java
import com.jcabi.github.*;
public class Main {
  public static void main(String[] args) throws IOException {
    Github github = new RtGithub(".. your OAuth token ..");
    Repo repo = github.repos().get(
        new Coordinates.Simple("octocat/Hello-World")
    );
    Issue issue = repo.issues().create("Test title", "Test description");
    issue.comments().post("My first comment!");
  }
}
```

## Work with Github Enterprise or other

If you want to work with Github's API through another domain, you can use the URI-constructors
of class ``RtGithub``. For instance, if you have your own instance of Github deployed under the
domain ``https://github.mydomain.com``, do the following:

```java
final Github github = new RtGithub(URI.create("https://github.mydomain.com"));

//OR

final Github github = new RtGithub(
    "<<oauth2_token>>",
    URI.create("https://github.mydomain.com")
);

//OR

final Github github = new RtGithub(
    "username", "password",
    URI.create("https://github.mydomain.com")
);
```

**DO NOT** change or mask your URIs! Using Github under a different domain is fine but do not
change the URI **paths**. Changing the requests' paths is not possible since the whole architecture
of this library relies on Github's URI paths.

For more complex configurations, you can instantiate ``RtGithub`` with your own custom ``Request``, by using the [RtGithub(Request)](https://github.com/jcabi/jcabi-github/blob/master/src/main/java/com/jcabi/github/RtGithub.java#L147) constructor. 
Be sure to configure the ``Request`` properly. See how the [default Request](https://github.com/jcabi/jcabi-github/blob/master/src/main/java/com/jcabi/github/RtGithub.java#L82) is created -- you basically have to do the same thing.

## Mock Implementation Of The API

We also provide [`MkGithub`](https://static.javadoc.io/com.jcabi/jcabi-github/1.0/com/jcabi/github/mock/MkGithub.html),
a mock version of the GitHub server, which
you can use in your unit tests, for example:

```java
import com.jcabi.github.*;
public class FooTest {
  public void submitsCommentToGithubIssue() {
    final Repo repo = new MkGithub().repos().create(
      Json.createObjectBuilder().add("name", "test").build()
    );
    final Issue issue = repo.issues().create("how are you?", "");
    new Foo(issue).doSomething(); // should post a message to the issue
    MasterAssert.assertThat(
      issue.comments().iterate(),
      Matchers.iterableWithSize(1)
    );
  }
}
```

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven (3.1 or higher!) build before submitting a pull request:

```
$ mvn clean install -Pqulice
```

There are many integration tests that check our classes against
live Github accounts. In order to run them, you should create
a new Github OAuth access tokens
([how?](https://help.github.com/articles/creating-an-access-token-for-command-line-use)),
and provide them in command line, like this:

```
$ mvn clean install -Dit.test=RtGistITCase -Dfailsafe.github.key=<token> -Dfailsafe.github.key.second=<second-token> -Dfailsafe.github.repo=<repo>
```

Replace `<token>` and `<second-token>` with the OAuth access tokens of two different Github
accounts. This test case will try to fork a gist from first account into second. Replace
`<repo>` with the name of repository you create in your first account (for test purposes
only), for example `yegor256/test`. OAuth access tokens should have permissions in their
respective repos to all scopes needed by the integration test suite you want to run
(including `delete_repo`, which is not set by default!).

Please note that different integration tests may need keys with permissions to different
[scopes](https://developer.github.com/v3/oauth/#scopes). To run all integration tests, the key should
have the following OAuth scopes:

  - `read:org`
  - `repo`
  - `delete_repo`
  - `admin:public_key`
  - `gist`
  - `admin:repo_hook`
  - `user`
  - `user:email`

`RtForksITCase` requires additional parameter `-Dfailsafe.github.organization=<organization>`
where `<organization>` is an organization name to fork test github repository.

In order to run static analysis checks only use this:

```
$ mvn clean install -DskipTests -Dinvoker.skip=true -Pqulice
```

