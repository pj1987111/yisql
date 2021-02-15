package com.zhy.yisql.common.utils.render

import java.io.{StringReader, StringWriter}

import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-05
  *  \* Time: 13:06
  *  \* Description: 
  *  \*/
object RenderEngine {
    def namedEvaluate(templateStr: String, root: Map[String, AnyRef]) = {
        val context: VelocityContext = new VelocityContext
        root.map { f =>
            context.put(f._1, f._2)
        }
        val w: StringWriter = new StringWriter
        Velocity.evaluate(context, w, "", new StringReader(templateStr))
        w.toString
    }
}
