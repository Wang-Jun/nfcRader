/* NFCard is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

NFCard is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Wget.  If not, see <http://www.gnu.org/licenses/>.

Additional permission under GNU GPL version 3 section 7 */

package com.otx.nfcreader.card.pboc;

import java.util.ArrayList;

import android.content.res.Resources;
import android.util.Log;

import com.otx.nfcreader.R;
import com.otx.nfcreader.Util;
import com.otx.nfcreader.tech.Iso7816;

final class BeijingMunicipal extends PbocCard {
	private final static int SFI_EXTRA_LOG = 4;
	private final static int SFI_EXTRA_CNT = 5;

	private BeijingMunicipal(Iso7816.Tag tag, Resources res) {
		super(tag);
		name = res.getString(R.string.name_bj);
	}

	public static String getHex(byte a[]){
		StringBuilder data=new StringBuilder();
		for(int i=0;i<a.length;i++){
			String hex = Integer.toHexString(a[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			data.append(hex+" ");
		}
		return data.toString();
	}
	@SuppressWarnings("unchecked")
	final static BeijingMunicipal load(Iso7816.Tag tag, Resources res) {

		Iso7816.Response MF,PSE,record,TEMP;
		MF=tag.selectByID(DFI_MF);
		Log.e("NFC MF",MF.getBytes().length+"");
		byte a[]=MF.getBytes();
		Log.e("NFC MFdata",getHex(a));
		Log.e("NFC MFdata String",new String(a));
		/*--------------------------------------------------------------*/
		// select PSF (1PAY.SYS.DDF01)
		/*--------------------------------------------------------------*/
		if ((PSE=tag.selectByName(DFN_PSE)).isOkey()) {
			Log.e("NFC PSEResponse",getHex(PSE.getBytes()));
			Log.e("NFC PSEResponse String",new String(PSE.getBytes()));
			Iso7816.Response INFO, CNT, CASH;

			/*--------------------------------------------------------------*/
			// read card info file, binary (4)
			/*--------------------------------------------------------------*/
			INFO = tag.readBinary(SFI_EXTRA_LOG);
			byte[] aa={(byte)0x10,(byte)0x02};
			/*search aid
			final Iso7816.Tag tag1=tag;
			new Thread(new Runnable() {
				@Override
				public void run() {
					byte[] aa={(byte)0x10,(byte)0x02};
					Iso7816.Response TEMP=tag1.selectByID(aa);
					for(int i=0;i<256;i++){
						for(int j=0;j<256;j++){
							aa[0]=(byte)(i&0xff);
							aa[1]=(byte)(j&0xff);
							TEMP=tag1.selectByID(aa);
							if(TEMP.isOkey()) {
								Log.e("search AID i=" + i + "j=" + j, Integer.toHexString(TEMP.getSw1() & 0xFF) + " " + Integer.toHexString(TEMP.getSw2() & 0xFF) + " length=" + TEMP.getBytes().length + " hex=" + getHex(TEMP.getBytes()) + " String=" + new String(TEMP.getBytes()));
							}
						}
						Log.e("search AID","a[0]="+i+" finished the last sw1sw2="+Integer.toHexString(TEMP.getSw1() & 0xFF) + " " + Integer.toHexString(TEMP.getSw2() & 0xFF));
					}
					tag1.close();
				}
			}).start();
			*/
			//如果进行shearch aid的话，可以在直接这里返回了，防止下面操作对search aid结果产生影响
			//if (false&&INFO.isOkey()) {
			  if (INFO.isOkey()) {
				/*--------------------------------------------------------------*/
				// read card operation file, binary (5)
				/*--------------------------------------------------------------*/
				CNT = tag.readBinary(SFI_EXTRA_CNT);
               /*scan MF
				for(int i=0;i<32;i++){
					TEMP=tag.readRecord(i,0x01);
					Log.e("NFC TEMP1 sfi="+i+" SW1SW2",Integer.toHexString(TEMP.getSw1() & 0xFF)+" "+Integer.toHexString(TEMP.getSw2() & 0xFF));
					if(TEMP.isOkey()){
						for(int j=1;j<11;j++){
							TEMP=tag.readRecord(i,j);
							if(TEMP.isOkey())
							Log.e("NFC TEMP1 sfi="+i+" Record "+j,"length="+TEMP.getBytes().length+" Hex= "+getHex(TEMP.getBytes()));
						}
					}
					//Log.e("NFC TEMP1 ",getHex(TEMP.getBytes())+"");
				}
				for(int i=0;i<32;i++){
					TEMP=tag.readBinary(i);
					Log.e("NFC TEMP1 sfi="+i+" SW1SW2",Integer.toHexString(TEMP.getSw1() & 0xFF)+" "+Integer.toHexString(TEMP.getSw2() & 0xFF));
					if(TEMP.isOkey())
						Log.e("NFC TEMP1 sfi="+i+" ReadBinary ","length="+TEMP.getBytes().length+" Hex= "+getHex(TEMP.getBytes()));
					//Log.e("NFC TEMP1 ",getHex(TEMP.getBytes())+"");
				}
				*/
				/*--------------------------------------------------------------*/
				// select Electronic Packet Application
				/*--------------------------------------------------------------*/
				byte[] pboc1={(byte)0x10,(byte)0x02};
				//if ((TEMP=tag.selectByID(pboc1)).isOkey()) {
				if ((TEMP=tag.selectByID(DFI_EP)).isOkey()) {

					Log.e("NFC TEMP1 xxx SW1SW2",Integer.toHexString(TEMP.getSw1() & 0xFF)+" "+Integer.toHexString(TEMP.getSw2() & 0xFF));
					Log.e("NFC TEMP1 xxx",getHex(TEMP.getBytes())+"");

				/*scan EP
				for(int i=0;i<32;i++){
					TEMP=tag.readRecord(i,0x01);
					Log.e("NFC TEMP1 sfi="+i+" SW1SW2",Integer.toHexString(TEMP.getSw1() & 0xFF)+" "+Integer.toHexString(TEMP.getSw2() & 0xFF));
					if(TEMP.isOkey()){
						for(int j=1;j<11;j++){
							TEMP=tag.readRecord(i,j);
							if(TEMP.isOkey())
							Log.e("NFC TEMP1 sfi="+i+" Record "+j,"length="+TEMP.getBytes().length+" Hex= "+getHex(TEMP.getBytes()));
						}
					}
					//Log.e("NFC TEMP1 ",getHex(TEMP.getBytes())+"");
				}
				for(int i=0;i<32;i++){
					TEMP=tag.readBinary(i);
					Log.e("NFC TEMP1 sfi="+i+" SW1SW2",Integer.toHexString(TEMP.getSw1() & 0xFF)+" "+Integer.toHexString(TEMP.getSw2() & 0xFF));
					if(TEMP.isOkey())
						Log.e("NFC TEMP1 sfi="+i+" ReadBinary ","length="+TEMP.getBytes().length+" Hex= "+getHex(TEMP.getBytes()));
					//Log.e("NFC TEMP1 ",getHex(TEMP.getBytes())+"");
				}
				*/
					/*--------------------------------------------------------------*/
					// read balance
					/*--------------------------------------------------------------*/
					CASH = tag.getBalance(true);

					/*--------------------------------------------------------------*/
					// read log file, record (24)
					/*--------------------------------------------------------------*/

					ArrayList<byte[]> LOG = readLog(tag, SFI_LOG);
					int num=0;
					for (final byte[] log : LOG) {
						if (log == null)
							continue;
						Log.e("NFC Record "+num++,getHex(log));
					}

					Log.e("NFC INFO",getHex(INFO.getBytes()));
					Log.e("NFC CASH",getHex(CASH.getBytes()));
					Log.e("NFC CNT",getHex(CNT.getBytes()));
					/*--------------------------------------------------------------*/
					// build result string
					/*--------------------------------------------------------------*/
					final BeijingMunicipal ret = new BeijingMunicipal(tag, res);
					ret.parseBalance(CASH);
					ret.parseInfo(INFO, CNT);
					ret.parseLog(LOG);

					return ret;
				}
			}
		}

		return null;
	}

	private void parseInfo(Iso7816.Response info, Iso7816.Response cnt) {
		if (!info.isOkey() || info.size() < 32) {
			serl = version = date = count = null;
			return;
		}

		final byte[] d = info.getBytes();
		serl = Util.toHexString(d, 0, 8);
		version = String.format("%02X%02X",  d[10],d[11]);
		date = String.format("%02X%02X.%02X.%02X - %02X%02X.%02X.%02X", d[24],
				d[25], d[26], d[27], d[28], d[29], d[30], d[31]);
		count = null;

		if (cnt != null && cnt.isOkey() && cnt.size() > 4) {
			byte[] e = cnt.getBytes();
			final int n = Util.toInt(e, 1, 4);
			if (e[0] == 0)
				count = String.format("%d ", n);
			else
				count = String.format("%d* ", n);
		}
	}
}
