<img src="http://img.jcabi.com/logo-square.png" width="64px" height="64px" />

[![Donate via Zerocracy](https://www.0crat.com/contrib-badge/C9R53K5JA.svg)](https://www.0crat.com/contrib/C9R53K5JA)

[![EO principles respected here](http://www.elegantobjects.org/badge.svg)](http://www.elegantobjects.org)
[![Managed by Zerocracy](https://www.0crat.com/badge/C3RUBL5H9.svg)](https://www.0crat.com/p/C3RUBL5H9)
[![DevOps By Rultor.com](http://www.rultor.com/b/jcabi/jcabi-github)](http://www.rultor.com/p/jcabi/jcabi-github)
[![We recommend IntelliJ IDEA](http://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Build Status](https://travis-ci.org/jcabi/jcabi-github.svg?branch=master)](https://travis-ci.org/jcabi/jcabi-github)
[![PDD status](http://www.0pdd.com/svg?name=jcabi/jcabi-github)](http://www.0pdd.com/p?name=jcabi/jcabi-github)
[![Build status](https://ci.appveyor.com/api/projects/status/rdhq60kvt75ic3cv/branch/master?svg=true)](https://ci.appveyor.com/project/yegor256/jcabi-github/branch/master)
[![JavaDoc](https://img.shields.io/badge/javadoc-html-blue.svg)](http://www.javadoc.io/doc/com.jcabi/jcabi-github)

[![jpeek report](http://i.jpeek.org/com.jcabi/jcabi-github/badge.svg)](http://i.jpeek.org/com.jcabi/jcabi-github/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-github/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-github)
[![Hits-of-Code](https://hitsofcode.com/github/jcabi/jcabi-github)](https://hitsofcode.com/view/github/jcabi/jcabi-github)

This is a Java adapter to the [GitHub RESTful API](https://developer.github.com/v3/).
There are a few other similar implementations on the market, but jcabi-github has a very strong
focus on object-oriented principles of programming. On top of that,
we have a unique implemenation of GitHub server-side functionality,
which you can use in your unit tests, eliminating the necessity to connect
to GitHub during unit/integration testing.
Please, read the blog post
[_Object-Oriented Github API_](http://www.yegor256.com/2014/05/14/object-oriented-github-java-sdk.html)
by [Yegor Bugayenko](https://www.yegor256.com), the creator of this library.

Java 8 or higher is required.

More details are here: [github.jcabi.com](http://github.jcabi.com/).

You may also get help in this [Telegram chat](https://t.me/elegantobjects).

The set of classes in the
[`com.jcabi.github`](https://static.javadoc.io/com.jcabi/jcabi-github/1.0/com/jcabi/github/package-frame.html)
package is the object-oriented API. Use it like this:

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

