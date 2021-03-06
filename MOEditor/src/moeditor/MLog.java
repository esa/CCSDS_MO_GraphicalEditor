/* ----------------------------------------------------------------------------
* Copyright (C) 2014      European Space Agency
*                         European Space Operations Centre
*                         Darmstadt
*                         Germany
* ----------------------------------------------------------------------------
* System                : CCSDS MO Graphical Service Editor
* ----------------------------------------------------------------------------
* Licensed under the European Space Agency Public License, Version 2.0
* You may not use this file except in compliance with the License.
*
* Except as expressly set forth in this License, the Software is provided to
* You on an "as is" basis and without warranties of any kind, including without
* limitation merchantability, fitness for a particular purpose, absence of
* defects or errors, accuracy or non-infringement of intellectual property rights.
* 
* See the License for the specific language governing permissions and
* limitations under the License.
* ----------------------------------------------------------------------------
*/

package moeditor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.ui.console.MessageConsoleStream;

public class MLog implements org.apache.maven.plugin.logging.Log
{
	private String log = "";
	
	private boolean memory = false;
	private ArrayList<String> bank = new ArrayList<String>();
	
	private int error = 0;
	private int info = 0;
	private int warn = 0;
	private int debug = 0;
	
	private String metaData;
	
	private MessageConsoleStream headingStream;
	private MessageConsoleStream debugStream;
	private MessageConsoleStream infoStream;
	private MessageConsoleStream warnStream;
	private MessageConsoleStream errorStream;
	
	public MLog(MessageConsoleStream heading, MessageConsoleStream debug, MessageConsoleStream info, MessageConsoleStream warn, MessageConsoleStream error)
	{
		setHeadingStream(heading);
		setDebugStream(debug);
		setInfoStream(info);
		setWarnStream(warn);
		setErrorStream(error);
		metaData = "";
	}
	
	@Override
	public void debug(CharSequence arg0)
	{
		if ( setLog("DEBUG " + arg0) ) debug++;
	}

	@Override
	public void debug(Throwable arg0)
	{
		if ( setLog("DEBUG " + arg0.getMessage()) ) debug++;
	}

	@Override
	public void debug(CharSequence arg0, Throwable arg1)
	{
		if ( setLog("DEBUG " + arg0 + "\n\t" +arg1.getMessage()) ) debug++;
	}

	@Override
	public void error(CharSequence arg0)
	{
		if ( setLog(errorStream, "ERROR " + arg0) ) error++;
	}
	
	public boolean errorB(CharSequence arg0)
	{
		boolean retval;
		retval = setLog(errorStream, "ERROR " + arg0);
		if ( retval ) error++;
		return retval;
	}

	@Override
	public void error(Throwable arg0)
	{
		if ( setLog(errorStream, "ERROR " + arg0.getMessage()) ) error++;
	}

	@Override
	public void error(CharSequence arg0, Throwable arg1)
	{
		if ( setLog(errorStream, "ERROR " + arg0 + "\n\t" +arg1.getMessage()) ) error++;
	}

	@Override
	public void info(CharSequence arg0)
	{
		if ( setLog(infoStream, "INFO " + arg0) ) info++;
	}

	@Override
	public void info(Throwable arg0)
	{
		if ( setLog(infoStream, "INFO " + arg0.getMessage()) ) info++;
	}

	@Override
	public void info(CharSequence arg0, Throwable arg1)
	{
		if ( setLog(infoStream, "INFO " + arg0 + "\n\t" +arg1.getMessage()) ) info++;
	}

	@Override
	public boolean isDebugEnabled()
	{
		return true;
	}

	@Override
	public boolean isErrorEnabled()
	{
		return true;
	}

	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	@Override
	public void warn(CharSequence arg0)
	{
		if ( setLog(warnStream, "WARN " + arg0) ) warn++;
	}

	@Override
	public void warn(Throwable arg0)
	{
		if ( setLog(warnStream, "WARN " + arg0.getMessage()) ) warn++;
	}

	@Override
	public void warn(CharSequence arg0, Throwable arg1)
	{
		if ( setLog(warnStream, "WARN " + arg0 + "\n\t" +arg1.getMessage()) ) warn++;
	}

	public boolean setLog(MessageConsoleStream out, String text)
	{
		boolean print = true;
		if ( hasMemory() )
		{
			if ( !bank.contains(text) )
			{
				bank.add(text);
			}
			else print = false;
		}
		if ( !print ) return print;

		out.println(text);
		log += text + "\n";
		return print;
	}

	public String getLog()
	{
		String retval = log;
		log = "";
		return retval;
	}
	
	public boolean setLog(String text)
	{
		return setLog(debugStream, text);
	}

	public void setHeading(String string)
	{
		metaData = string;
		getHeadingStream().println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " " + string + " started");
	}
	
	public void setFooting()
	{
		getHeadingStream().println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " " + metaData + " Finished with");
	}

	public boolean hasMemory()
	{
		return memory;
	}

	public void giveMemory(boolean memory)
	{
		this.memory = memory;
	}
	
	private void clearMemory()
	{
		error = 0;
		info = 0;
		warn = 0;
		debug = 0;
	}

	public int getDebug()
	{
		return debug;
	}

	public int getInfo()
	{
		return info;
	}

	public int getWarn()
	{
		return warn;
	}

	public int getError()
	{
		return error;
	}

	public void printStat()
	{
		setFooting();
		
		getDebugStream().println(debug + " Debug messages");
		getInfoStream().println(info + " Information messages");
		getWarnStream().println(warn + " Warnings");
		getErrorStream().println(error + " Errors");
		
		clearMemory();
	}
	
	public MessageConsoleStream getDebugStream()
	{
		return debugStream;
	}

	public void setDebugStream(MessageConsoleStream debugStream)
	{
		this.debugStream = debugStream;
		this.debugStream.setColor(ColorConstants.black);
	}

	public MessageConsoleStream getInfoStream()
	{
		return infoStream;
	}

	public void setInfoStream(MessageConsoleStream infoStream)
	{
		this.infoStream = infoStream;
		this.infoStream.setColor(ColorConstants.darkGreen);
	}

	public MessageConsoleStream getWarnStream()
	{
		return warnStream;
	}

	public void setWarnStream(MessageConsoleStream warnStream)
	{
		this.warnStream = warnStream;
		this.warnStream.setColor(new org.eclipse.swt.graphics.Color(MultiPageEditor.getDisplay(), 154, 0, 143));//ColorConstants.orange);
	}

	public MessageConsoleStream getErrorStream()
	{
		return errorStream;
	}

	public void setErrorStream(MessageConsoleStream errorStream)
	{
		this.errorStream = errorStream;
		this.errorStream.setColor(ColorConstants.red);
	}

	public MessageConsoleStream getHeadingStream()
	{
		return headingStream;
	}

	public void setHeadingStream(MessageConsoleStream headingStream)
	{
		this.headingStream = headingStream;
		this.headingStream.setColor(ColorConstants.black);
		this.headingStream.setFontStyle(SWT.BOLD);
	}
}
