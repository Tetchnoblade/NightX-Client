package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Hud
import net.aspw.client.visual.hud.element.elements.Notification

class MagicTrickCommand : Command("magictrick", arrayOf("mt")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        try {
            mc.thePlayer.sendChatMessage("!おい、事故だ、事故だ！おめぇらの仲間のストーカーが来たぁ！いや待てよ、まあええわ、来んでもええわ。先に証拠の写真撮ってからだ！普通の子だ。俺隣だよ？言っとくけど")
            mc.thePlayer.sendChatMessage("!めちゃんこ早いですわ、おんもしれぇ奴らだなこのキチガイ共！どゆこと？おおい、壊れるだろ！湯だぞ！あったかいぞ！110番すると金になるんか、おめぇか犯人はボケェ！")
            mc.thePlayer.sendChatMessage("!おーはえ！はえーもう来ましたどっかに隠れてたみたいに！俺はお前が俺を見たの見たぞ！みなさま、いちにちお気をつけてお過ごしください。危ねぇ、糞がばーか！アーロン！")
            mc.thePlayer.sendChatMessage("!片付けろなんて言われました、バーカ創価学会員！コラだとよ、キチゲェが！クソネコヤマト！20分経ったら出るとは言ってねぇでな！やかましいわアーロー！")
            mc.thePlayer.sendChatMessage("!ゴロツキサーモンは、、、よく群れる！じゃああなた動画やりなさいよ！服ぐらい着せろよ！バーカ！よく頑張れって言われるんですけど、俺自分のためにやってるんでやめないです")
            mc.thePlayer.sendChatMessage("!集団ストーカーのことを広めるビラは要りませんかー？ <- しょうもねぇことやってんなって、用もねぇのに人んち来たら水かけられる。かーさーん！警察呼んでー！かーさんもグルかもしれん")
            mc.thePlayer.sendChatMessage("!すいません、ストーカー被害者なんです。ゼロではねぇか、ゼロではねぇけど...なんの騒ぎだ、なんの揺れだ！隙あらば助けてやってください")
            mc.thePlayer.sendChatMessage("!助けてー！集団ストーカーに襲われてまーす！だから照らすなっつっとるだろうが糞が！無事に返してもるぁいました～アップロードしま～す。あれぇ、あれぇ！？(涙)")
            mc.thePlayer.sendChatMessage("!ちょっとした観光地になっとるんですここが。どぅーらちょっと見せてみろ酒ぇ。何やってんだ一体！挟むやつ壊れるだろ！ライトテラシー！テラテラ！")
            mc.thePlayer.sendChatMessage("!動画をとにかくやめる、動画。さすればそういうことにならん！だからもうインターネット切る、切らないかんな！来訪者止まらんぞこれ、やめやめやめなさいって言ってんの！")
            if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
            }
            Client.hud.addNotification(
                Notification(
                    "Sent Chat Successfully!",
                    Notification.Type.SUCCESS
                )
            )
            return
        } catch (ex: NumberFormatException) {
            chatSyntaxError()
        }

        return
    }
}
