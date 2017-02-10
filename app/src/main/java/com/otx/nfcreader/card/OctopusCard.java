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

package com.otx.nfcreader.card;

import android.content.res.Resources;
import android.nfc.tech.NfcF;

import com.otx.nfcreader.R;
import com.otx.nfcreader.Util;
import com.otx.nfcreader.tech.FeliCa;

public class OctopusCard {
	private static final int SYS_SZT = 0x8005;
	private static final int SRV_SZT = 0x0118;
	private static final int SYS_OCTOPUS = 0x8008;
	private static final int SRV_OCTOPUS = 0x0117;

	String name;
	String cash;
	String Id;
	String Pm;

	public String getCardCash() {
		return cash;
	}

	public String getCardId() {
		return Id;
	}

	public String getCardName() {
		return name;
	}

	public String getCardPm() {
		return Pm;
	}

	public void load(NfcF tech, Resources res) {
		final FeliCa.Tag tag = new FeliCa.Tag(tech);

		/*--------------------------------------------------------------*/
		// check card system
		/*--------------------------------------------------------------*/

		final int system = tag.getSystemCode();
		final FeliCa.ServiceCode service;
		if (system == SYS_OCTOPUS)
			service = new FeliCa.ServiceCode(SRV_OCTOPUS);
		else if (system == SYS_SZT)
			service = new FeliCa.ServiceCode(SRV_SZT);
		else
			return;

		tag.connect();

		/*--------------------------------------------------------------*/
		// read service data without encryption
		/*--------------------------------------------------------------*/

		final float[] data = new float[] { 0, 0, 0 };
		final int N = data.length;

		int p = 0;
		for (byte i = 0; p < N; ++i) {
			final FeliCa.ReadResponse r = tag.readWithoutEncryption(service, i);
			if (!r.isOkey())
				break;

			data[p++] = (Util.toInt(r.getBlockData(), 0, 4) - 350) / 10.0f;
		}

		tag.close();
		name=parseName(system, res);
		parseInfo(tag, res);
		cash=parseBalance(data, p, res);
	}

	private static String parseName(int system, Resources res) {
		if (system == SYS_OCTOPUS)
			return res.getString(R.string.name_octopuscard);

		if (system == SYS_SZT)
			return res.getString(R.string.name_szt_f);

		return "";
	}

	protected void parseInfo(FeliCa.Tag tag, Resources res) {
		final String i = res.getString(R.string.lab_id);
		final String p = res.getString(R.string.lab_pmm);
		Id=tag.getIDm().toString();
		Pm=tag.getPMm().toString();
	}

	private static String parseBalance(float[] value, int count, Resources res) {
		if (count < 1)
			return "";

		final StringBuilder r = new StringBuilder();
		for (int i = 0; i < count; ++i)
			r.append(Util.toAmountString(value[i])).append(' ');

		return r.toString();
	}

	private static String parseLog(byte[] data, Resources res) {
		return null;
	}
}
