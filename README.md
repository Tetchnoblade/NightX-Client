# NightX Client (LiquidBounce Custom Build)

Why i should be NightX Developer?
> https://www.youtube.com/watch?v=8q8pYpbslwg

If you have request and issue -> Join discord server to get support! ↓
> https://nightx.api-minecraft.net/s/rgul30dkqw

If you need configs try access this site!
> https://nightx.api-minecraft.net/s/zf45j6uzog

And please subscribe to my youtube channel!
> https://www.youtube.com/@as_pw5147

`(Made in Japan :r)`

## License of Using Our Client
This project is subject to the [GNU General Public License v3.0](LICENSE). This does only apply for source code located directly in this clean repository. During the development and compilation process, additional source code may be used to which we have obtained no rights. Such code is not covered by the GPL license.

For those who are unfamiliar with the license, here is a summary of its main points. This is by no means legal advice nor legally binding.

You are allowed to
- use
- share
- modify

this project entirely or partially for free and even commercially. However, please consider the following:

- **You must disclose the source code of your modified work and the source code you took from this project. This means you are not allowed to use code from this project (even partially) in a closed-source (or even obfuscated) application.**
- **Your modified application must also be licensed under the GPL.**

Do the above and share your source code with everyone; just like we do.

## Setting up a Workspace
LiquidBounce+ is using Gradle, so make sure that it is installed properly. Instructions can be found on [Gradle's website](https://gradle.org/install/).
1. Clone the repository using `git clone https://github.com/WYSI-Foundation/LiquidBouncePlus/`.
2. CD into the local repository folder.
4. Depending on which IDE you are using execute either of the following commands:
    - For IntelliJ: `gradlew setupDevWorkspace idea genIntellijRuns build`
    - For Eclipse: `gradlew setupDevWorkspace eclipse build`

   (you can add `-debug` right after `gradlew` if you want to enable debug logging.)
5. Open the folder as a Gradle project in your IDE.
6. Select the default run configuration.

## Additional libraries
### Mixins
Mixins can be used to modify classes at runtime before they are loaded. LiquidBounce+ is using it to inject its code into the Minecraft client. This way, we do not have to ship Mojang's copyrighted code. If you want to learn more about it, check out its [Documentation](https://docs.spongepowered.org/5.1.0/en/plugin/internals/mixins.html).