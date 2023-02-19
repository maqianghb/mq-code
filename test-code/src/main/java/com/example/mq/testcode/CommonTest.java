package com.example.mq.testcode;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.common.utils.CloseableHttpClientUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class CommonTest {

    @Test
    public void testGetAllStockCodes(){
        try {
            List<String> strList =FileUtils.readLines(new File("E:/stock_list_2_sz.txt"), Charset.forName("UTF-8"));
            if(CollectionUtils.isEmpty(strList)){
                return;
            }

            List<String> formatCodeList =Lists.newArrayList();
            for(String str : strList){
                String[] split = StringUtils.split(str, ", ");
                if(split ==null || split.length <2){
                    continue;
                }

                String code =split[0];
                String formatCode =new StringBuilder()
                        .append("SZ").append(code)
                        .toString();
                formatCodeList.add(formatCode);
            }
            FileUtils.writeLines(new File("E:/stock_code_2_sz.txt"), formatCodeList, true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseDate(){
        Long reportDateMillis = 1640880000000L;
        LocalDateTime localDateTime = LocalDateTime.ofInstant(new Date(reportDateMillis).toInstant(), ZoneId.systemDefault());
        Integer year = localDateTime.getYear();
        Integer month = localDateTime.getMonthValue();

        System.out.println("year: " + year);
        System.out.println("month: " + month);
    }

    @Test
    public void testParseObject(){
        List<String> keyList = Arrays.asList("total_assets", "total_liab", "asset_liab_ratio", "total_quity_atsopc");
        String strBalanceResult ="{\"data\":{\"quote_name\":\"海康威视\",\"currency_name\":\"人民币\",\"org_type\":1,\"last_report_name\":\"2022三季报\",\"statuses\":null,\"currency\":\"CNY\",\"list\":[{\"report_date\":1664467200000,\"report_name\":\"2022三季报\",\"ctime\":null,\"total_assets\":[1.1026439800269E11,0.14538930684316367],\"total_liab\":[4.23822361163E10,0.13864908201025455],\"asset_liab_ratio\":[0.3843691788465217,-0.0058846584236813045],\"total_quity_atsopc\":[6.530950941509E10,0.1352033061290585],\"tradable_fnncl_assets\":[6.178347839E7,7.896764357407531],\"interest_receivable\":[null,null],\"saleable_finacial_assets\":[null,null],\"held_to_maturity_invest\":[null,null],\"fixed_asset\":[null,null],\"intangible_assets\":[1.53533493804E9,0.26065164709444283],\"construction_in_process\":[null,null],\"dt_assets\":[1.46806391011E9,0.6495550030463958],\"tradable_fnncl_liab\":[4.955336669E7,16.639324551395173],\"payroll_payable\":[4.13541428227E9,0.08376166195260143],\"tax_payable\":[1.58819218542E9,-0.2834725109888761],\"estimated_liab\":[2.182233187E8,0.39739001967757565],\"dt_liab\":[7.752533359E7,-0.07148027444558805],\"bond_payable\":[null,null],\"shares\":[9.433208719E9,0.010433229204914055],\"capital_reserve\":[9.04566697581E9,0.6950381861320065],\"earned_surplus\":[4.672505348E9,0.0],\"undstrbtd_profit\":[4.549924549187E10,0.1573261685282983],\"minority_equity\":[2.5726524713E9,0.6976367814304394],\"total_holders_equity\":[6.788216188639E10,0.14963818095278944],\"total_liab_and_holders_equity\":[1.1026439800269E11,0.14538930684316367],\"lt_equity_invest\":[1.10700452375E9,0.2929838125960131],\"derivative_fnncl_liab\":[null,null],\"general_risk_provision\":[null,null],\"frgn_currency_convert_diff\":[null,null],\"goodwill\":[2.1842851134E8,-0.42594307792466046],\"invest_property\":[null,null],\"interest_payable\":[null,null],\"treasury_stock\":[3.30413599299E9,2.229253722786417],\"othr_compre_income\":[-3.69811266E7,0.6467019171302646],\"othr_equity_instruments\":[null,null],\"currency_funds\":[3.034182351442E10,0.06026462783193967],\"bills_receivable\":[2.06697427575E9,0.7001533390036555],\"account_receivable\":[3.177168855453E10,0.21434471254996196],\"pre_payment\":[5.4588415419E8,-0.15186281756139036],\"dividend_receivable\":[null,null],\"othr_receivables\":[null,null],\"inventory\":[1.951373554998E10,0.0241786293798122],\"nca_due_within_one_year\":[1.05662064105E9,-0.17966443900050963],\"othr_current_assets\":[9.6442385013E8,-0.1587032955076204],\"current_assets_si\":[null,null],\"total_current_assets\":[8.934228982747E10,0.11434926324572173],\"lt_receivable\":[5.8024813795E8,-0.7420324505837982],\"dev_expenditure\":[null,null],\"lt_deferred_expense\":[1.7416441591E8,0.7714629430962552],\"othr_noncurrent_assets\":[3.21804806151E9,2.2820396933142755],\"noncurrent_assets_si\":[null,null],\"total_noncurrent_assets\":[2.092210817522E10,0.30002281239572626],\"st_loan\":[3.46634721025E9,0.03424813821262458],\"bill_payable\":[9.2985065919E8,0.4525295024987702],\"accounts_payable\":[1.297546045308E10,-0.08878188733965298],\"pre_receivable\":[null,null],\"dividend_payable\":[null,null],\"othr_payables\":[null,null],\"noncurrent_liab_due_in1y\":[5.9439663398E8,-0.8357996879778343],\"current_liab_si\":[null,null],\"total_current_liab\":[3.028443406284E10,-0.10533617398493826],\"lt_loan\":[7.49787911681E9,2.522305879856183],\"lt_payable\":[null,null],\"special_payable\":[null,null],\"othr_non_current_liab\":[2.83203709911E9,4.535708194077284],\"noncurrent_liab_si\":[null,null],\"total_noncurrent_liab\":[1.209780205346E10,2.588321995803028],\"salable_financial_assets\":[null,null],\"othr_current_liab\":[8.6914055333E8,0.14100000032448265],\"ar_and_br\":[3.383866283028E10,0.23591660140774784],\"contractual_assets\":[1.36489519593E9,3.825181734575773],\"bp_and_ap\":[1.390531111227E10,-0.06549363159981336],\"contract_liabilities\":[2.49236548693E9,-0.045754850659966144],\"to_sale_asset\":[null,null],\"other_eq_ins_invest\":[null,null],\"other_illiquid_fnncl_assets\":[3.8221315624E8,-0.1631509060444976],\"fixed_asset_sum\":[7.77242880023E9,0.21931809512480696],\"fixed_assets_disposal\":[null,null],\"construction_in_process_sum\":[3.88657780185E9,0.8107314910355331],\"project_goods_and_material\":[null,null],\"productive_biological_assets\":[null,null],\"oil_and_gas_asset\":[null,null],\"to_sale_debt\":[null,null],\"lt_payable_sum\":[7735344.18,-0.16465785935862295],\"noncurrent_liab_di\":[1.11597906356E9,3.0745260315062803],\"perpetual_bond\":[null,null],\"special_reserve\":[null,null]},{\"report_date\":1656518400000,\"report_name\":\"2022中报\",\"ctime\":null,\"total_assets\":[1.0489433385954E11,0.20403746223835398],\"total_liab\":[4.050414822381E10,0.2223281716131881],\"asset_liab_ratio\":[0.3861423847550008,0.015191146412364138],\"total_quity_atsopc\":[6.201380860879E10,0.17215352827213637],\"tradable_fnncl_assets\":[4.803548036E7,0.4725747926961146],\"interest_receivable\":[null,null],\"saleable_finacial_assets\":[null,null],\"held_to_maturity_invest\":[null,null],\"fixed_asset\":[7.75670867069E9,0.23936291559196934],\"intangible_assets\":[1.52468895696E9,0.2383006821774464],\"construction_in_process\":[2.84520942358E9,0.6681355876976309],\"dt_assets\":[1.42544750025E9,0.6105444423134709],\"tradable_fnncl_liab\":[1.097702589E8,29.06134553953083],\"payroll_payable\":[3.37312158234E9,0.1939106817134731],\"tax_payable\":[1.35976259198E9,-0.16610128365835727],\"estimated_liab\":[2.2128957247E8,0.3908952652472217],\"dt_liab\":[7.62998765E7,-0.15384354452540835],\"bond_payable\":[null,null],\"shares\":[9.433208719E9,0.010433229204914055],\"capital_reserve\":[8.84337778209E9,0.7061983981589127],\"earned_surplus\":[4.672505348E9,0.0],\"undstrbtd_profit\":[4.241824437968E10,0.21786899335820675],\"minority_equity\":[2.37637702694E9,1.2083883539724967],\"total_holders_equity\":[6.439018563573E10,0.1928096869570697],\"total_liab_and_holders_equity\":[1.0489433385954E11,0.20403746223835398],\"lt_equity_invest\":[1.1128669906E9,0.29221836730963113],\"derivative_fnncl_liab\":[null,null],\"general_risk_provision\":[null,null],\"frgn_currency_convert_diff\":[null,null],\"goodwill\":[2.1399676047E8,-0.1728884056598762],\"invest_property\":[null,null],\"interest_payable\":[null,null],\"treasury_stock\":[3.30413599299E9,2.229253722786417],\"othr_compre_income\":[-4.939162699E7,0.4644565701694301],\"othr_equity_instruments\":[null,null],\"currency_funds\":[2.802675355436E10,0.01061942493999654],\"bills_receivable\":[1.28801370627E9,0.14463898321420743],\"account_receivable\":[2.911316344764E10,0.22312330447290984],\"pre_payment\":[6.2346509039E8,0.6064291651096027],\"dividend_receivable\":[6.086687542E7,null],\"othr_receivables\":[6.4015824913E8,-0.20261351964864482],\"inventory\":[2.06752294721E10,0.3697189051583077],\"nca_due_within_one_year\":[9.2905041726E8,-0.19269511496334576],\"othr_current_assets\":[1.15067707225E9,0.34284203773899063],\"current_assets_si\":[null,null],\"total_current_assets\":[8.52060866558E10,0.1786706618744432],\"lt_receivable\":[5.8577256378E8,-0.6939785117043448],\"dev_expenditure\":[null,null],\"lt_deferred_expense\":[1.5791108323E8,0.5798707012893153],\"othr_noncurrent_assets\":[3.11085644455E9,3.062349132611455],\"noncurrent_assets_si\":[null,null],\"total_noncurrent_assets\":[1.968824720374E10,0.3276995552945232],\"st_loan\":[4.58800345026E9,0.4597909937550885],\"bill_payable\":[7.7778605256E8,-0.35704977536682525],\"accounts_payable\":[1.47583289014E10,0.18557232917602406],\"pre_receivable\":[null,null],\"dividend_payable\":[8.6945294087E8,0.33426195706075473],\"othr_payables\":[1.73841597334E9,0.24550647076506488],\"noncurrent_liab_due_in1y\":[6.7233975276E8,-0.8090063698410005],\"current_liab_si\":[null,null],\"total_current_liab\":[3.149646084993E10,0.0475637421589647],\"lt_loan\":[4.73078432212E9,1.4755347017769183],\"lt_payable\":[null,null],\"special_payable\":[null,null],\"othr_non_current_liab\":[2.83322837294E9,4.538036745600918],\"noncurrent_liab_si\":[null,null],\"total_noncurrent_liab\":[9.00768737388E9,1.9336277337676158],\"salable_financial_assets\":[null,null],\"othr_current_liab\":[8.6417007925E8,0.15581570123055058],\"ar_and_br\":[3.040117715391E10,0.2195804369935702],\"contractual_assets\":[1.35108706582E9,6.333336158937341],\"bp_and_ap\":[1.553611495396E10,0.13751124614942972],\"contract_liabilities\":[2.38530926627E9,-0.04230408490540967],\"to_sale_asset\":[null,null],\"other_eq_ins_invest\":[null,null],\"other_illiquid_fnncl_assets\":[3.8327545644E8,-0.18219172240583478],\"fixed_asset_sum\":[7.75670867069E9,0.23936291559196934],\"fixed_assets_disposal\":[null,null],\"construction_in_process_sum\":[2.84520942358E9,0.6681355876976309],\"project_goods_and_material\":[null,null],\"productive_biological_assets\":[null,null],\"oil_and_gas_asset\":[null,null],\"to_sale_debt\":[null,null],\"lt_payable_sum\":[7662432.39,-0.08665668087436929],\"noncurrent_liab_di\":[7.7667517734E8,3.0721203277600444],\"perpetual_bond\":[null,null],\"special_reserve\":[null,null]},{\"report_date\":1648656000000,\"report_name\":\"2022一季报\",\"ctime\":null,\"total_assets\":[1.0443315485205E11,0.20901685341473472],\"total_liab\":[3.640365502339E10,0.23561990222138482],\"asset_liab_ratio\":[0.34858331221500394,0.02200386928562062],\"total_quity_atsopc\":[6.593558842684E10,0.17838292106079975],\"tradable_fnncl_assets\":[3.960253735E7,0.3672069301781217],\"interest_receivable\":[null,null],\"saleable_finacial_assets\":[null,null],\"held_to_maturity_invest\":[null,null],\"fixed_asset\":[null,null],\"intangible_assets\":[1.29714378157E9,0.0411064726173995],\"construction_in_process\":[null,null],\"dt_assets\":[1.20826298073E9,0.4833433805852296],\"tradable_fnncl_liab\":[3.514273059E7,15.991523522793813],\"payroll_payable\":[2.44427016484E9,0.6697016314364233],\"tax_payable\":[1.72716817861E9,-0.1786550897921933],\"estimated_liab\":[1.9553079087E8,0.47759657758628665],\"dt_liab\":[9.292872813E7,-0.001309417983274381],\"bond_payable\":[null,null],\"shares\":[9.433208719E9,0.009610138044151702],\"capital_reserve\":[8.41100511494E9,0.6050133764404396],\"earned_surplus\":[4.672505348E9,0.0],\"undstrbtd_profit\":[4.74329498282E10,0.24902555877344076],\"minority_equity\":[2.09391140182E9,1.1756649360827949],\"total_holders_equity\":[6.802949982866E10,0.1952462924024917],\"total_liab_and_holders_equity\":[1.0443315485205E11,0.20901685341473472],\"lt_equity_invest\":[1.05873912027E9,0.2415948170137842],\"derivative_fnncl_liab\":[null,null],\"general_risk_provision\":[null,null],\"frgn_currency_convert_diff\":[null,null],\"goodwill\":[2.0992267857E8,-0.23514572937823666],\"invest_property\":[null,null],\"interest_payable\":[null,null],\"treasury_stock\":[3.91702011759E9,2.2909071263106386],\"othr_compre_income\":[-9.706046571E7,-0.10557579124929078],\"othr_equity_instruments\":[null,null],\"currency_funds\":[3.421478996783E10,0.06551578240277794],\"bills_receivable\":[1.48211355459E9,0.12690998972606746],\"account_receivable\":[2.578966660819E10,0.22962122331512988],\"pre_payment\":[5.2009248858E8,0.45223492600920934],\"dividend_receivable\":[null,null],\"othr_receivables\":[null,null],\"inventory\":[1.850014173063E10,0.3913850999616314],\"nca_due_within_one_year\":[9.9982368549E8,-0.13249002571830135],\"othr_current_assets\":[1.05968722243E9,0.660602865175115],\"current_assets_si\":[null,null],\"total_current_assets\":[8.597865246982E10,0.1929324660327423],\"lt_receivable\":[5.649643929E8,-0.702492631473428],\"dev_expenditure\":[null,null],\"lt_deferred_expense\":[1.5106237439E8,0.4319509055698943],\"othr_noncurrent_assets\":[3.31612295505E9,4.258110782411922],\"noncurrent_assets_si\":[null,null],\"total_noncurrent_assets\":[1.845450238223E10,0.29005414140709174],\"st_loan\":[5.17838111737E9,0.4168537521472444],\"bill_payable\":[1.45080879113E9,0.16153950629043154],\"accounts_payable\":[1.136455086239E10,0.15737700796240114],\"pre_receivable\":[null,null],\"dividend_payable\":[null,null],\"othr_payables\":[null,null],\"noncurrent_liab_due_in1y\":[6.4384841636E8,-0.8153832847739992],\"current_liab_si\":[null,null],\"total_current_liab\":[2.802377694972E10,0.06672965979195927],\"lt_loan\":[3.56890079146E9,0.841477705484637],\"lt_payable\":[null,null],\"special_payable\":[null,null],\"othr_non_current_liab\":[3.42789791423E9,4.7599365515363266],\"noncurrent_liab_si\":[null,null],\"total_noncurrent_liab\":[8.37987807367E9,1.626002283561974],\"salable_financial_assets\":[null,null],\"othr_current_liab\":[9.2404016241E8,0.13244156751983172],\"ar_and_br\":[2.727178016278E10,0.22356053196063408],\"contractual_assets\":[1.49697317513E9,6.636681891697005],\"bp_and_ap\":[1.281535965352E10,0.15784674039117094],\"contract_liabilities\":[2.35111606738E9,0.15452108738948936],\"to_sale_asset\":[null,null],\"other_eq_ins_invest\":[null,null],\"other_illiquid_fnncl_assets\":[3.9855043823E8,-0.2259648056902269],\"fixed_asset_sum\":[7.325592362E9,0.24720781390890811],\"fixed_assets_disposal\":[null,null],\"construction_in_process_sum\":[2.35925116351E9,0.3754594017181981],\"project_goods_and_material\":[null,null],\"productive_biological_assets\":[null,null],\"oil_and_gas_asset\":[null,null],\"to_sale_debt\":[null,null],\"lt_payable_sum\":[7991823.98,-0.044246817100189184],\"noncurrent_liab_di\":[7.5823786619E8,3.435713962518536],\"perpetual_bond\":[null,null],\"special_reserve\":[null,null]},{\"report_date\":1640880000000,\"report_name\":\"2021年报\",\"ctime\":null,\"total_assets\":[1.0386454319518E11,0.17094220090780243],\"total_liab\":[3.84699009193E10,0.1241297852137567],\"asset_liab_ratio\":[0.3703853089403975,-0.03997841708818176],\"total_quity_atsopc\":[6.346088666526E10,0.1796951256442415],\"tradable_fnncl_assets\":[3.432001083E7,0.5132382144396648],\"interest_receivable\":[null,null],\"saleable_finacial_assets\":[null,null],\"held_to_maturity_invest\":[null,null],\"fixed_asset\":[null,null],\"intangible_assets\":[1.30424741507E9,0.042298995625281686],\"construction_in_process\":[null,null],\"dt_assets\":[1.21087757524E9,0.4759942537264766],\"tradable_fnncl_liab\":[4062317.57,-0.45146595976031484],\"payroll_payable\":[4.59555207312E9,0.5969051852072974],\"tax_payable\":[1.46147002969E9,-0.1743377306624877],\"estimated_liab\":[2.0067595096E8,0.3250846640964315],\"dt_liab\":[9.331515117E7,0.0036064520878928627],\"bond_payable\":[null,null],\"shares\":[9.335806114E9,-8.145923322514083E-4],\"capital_reserve\":[5.40407060007E9,0.0435031510099061],\"earned_surplus\":[4.672505348E9,0.0],\"undstrbtd_profit\":[4.514887745152E10,0.2609120525201539],\"minority_equity\":[1.93375561062E9,1.8212206867888838],\"total_holders_equity\":[6.539464227588E10,0.20034783928941574],\"total_liab_and_holders_equity\":[1.0386454319518E11,0.17094220090780243],\"lt_equity_invest\":[9.8216554645E8,0.13673053717118536],\"derivative_fnncl_liab\":[null,null],\"general_risk_provision\":[null,null],\"frgn_currency_convert_diff\":[null,null],\"goodwill\":[2.0238189537E8,-0.2619285551037922],\"invest_property\":[null,null],\"interest_payable\":[null,null],\"treasury_stock\":[1.02318872304E9,-0.08800103887438647],\"othr_compre_income\":[-7.718412529E7,0.0918865845028338],\"othr_equity_instruments\":[null,null],\"currency_funds\":[3.472187093136E10,-0.020808342180423232],\"bills_receivable\":[1.5227609053E9,0.1684310335484767],\"account_receivable\":[2.617477310042E10,0.19087855284028932],\"pre_payment\":[5.0579825335E8,0.7068479346409248],\"dividend_receivable\":[null,null],\"othr_receivables\":[null,null],\"inventory\":[1.79741124076E10,0.565974868923375],\"nca_due_within_one_year\":[9.7596043714E8,-0.025217892952235935],\"othr_current_assets\":[1.02260037778E9,1.053766990402945],\"current_assets_si\":[null,null],\"total_current_assets\":[8.601922461663E10,0.15056021510149595],\"lt_receivable\":[6.1306794497E8,-0.708835164040605],\"dev_expenditure\":[null,null],\"lt_deferred_expense\":[1.580071749E8,0.45515154561593707],\"othr_noncurrent_assets\":[3.35052641163E9,3.64376244690872],\"noncurrent_assets_si\":[null,null],\"total_noncurrent_assets\":[1.784531857855E10,0.28026430925886003],\"st_loan\":[4.07496246997E9,0.01893252462229351],\"bill_payable\":[1.33999838334E9,0.2922868555991457],\"accounts_payable\":[1.588969498112E10,0.16888551185800596],\"pre_receivable\":[null,null],\"dividend_payable\":[null,null],\"othr_payables\":[null,null],\"noncurrent_liab_due_in1y\":[5.9691536058E8,-0.8298261806212825],\"current_liab_si\":[null,null],\"total_current_liab\":[3.329165634762E10,0.06618889823152903],\"lt_loan\":[3.28437164252E9,0.6747020358640239],\"lt_payable\":[null,null],\"special_payable\":[null,null],\"othr_non_current_liab\":[5.3433415827E8,-0.04746370584701045],\"noncurrent_liab_si\":[null,null],\"total_noncurrent_liab\":[5.17824457168E9,0.7277947637197231],\"salable_financial_assets\":[null,null],\"othr_current_liab\":[9.1747992261E8,0.2303415257934533],\"ar_and_br\":[2.769753400572E10,0.18962204591035806],\"contractual_assets\":[1.41137262491E9,4.743018182176361],\"bp_and_ap\":[1.722969336446E10,0.17763126094977463],\"contract_liabilities\":[2.58089422659E9,0.19421341301977926],\"to_sale_asset\":[null,null],\"other_eq_ins_invest\":[null,null],\"other_illiquid_fnncl_assets\":[4.3872417222E8,-0.10817375278876365],\"fixed_asset_sum\":[6.69559067127E9,0.13947959214909902],\"fixed_assets_disposal\":[null,null],\"construction_in_process_sum\":[2.32333609868E9,0.6301422452359393],\"project_goods_and_material\":[null,null],\"productive_biological_assets\":[null,null],\"oil_and_gas_asset\":[null,null],\"to_sale_debt\":[null,null],\"lt_payable_sum\":[9009331.5,-0.7724655390315607],\"noncurrent_liab_di\":[7.3858645805E8,2.869396348903069],\"perpetual_bond\":[null,null],\"special_reserve\":[null,null]},{\"report_date\":1632931200000,\"report_name\":\"2021三季报\",\"ctime\":null,\"total_assets\":[9.62680525686E10,0.21524894260892752],\"total_liab\":[3.722150817658E10,0.20604714109249225],\"asset_liab_ratio\":[0.386644449362432,-0.007571947766258144],\"total_quity_atsopc\":[5.75311127641E10,0.20355998910918252],\"tradable_fnncl_assets\":[6944488.57,0.8773340200763776],\"interest_receivable\":[null,null],\"saleable_finacial_assets\":[null,null],\"held_to_maturity_invest\":[null,null],\"fixed_asset\":[null,null],\"intangible_assets\":[1.2178899235E9,0.03148573264926341],\"construction_in_process\":[null,null],\"dt_assets\":[8.8997572521E8,0.004541040317657873],\"tradable_fnncl_liab\":[2809255.34,400.07295323620315],\"payroll_payable\":[3.81579680058E9,0.7068209930516939],\"tax_payable\":[2.21651256899E9,0.23248139152644903],\"estimated_liab\":[1.5616493293E8,0.830691998026202],\"dt_liab\":[8.349346972E7,-0.12128650198385302],\"bond_payable\":[null,null],\"shares\":[9.335806114E9,-9.84972869420031E-4],\"capital_reserve\":[5.33655645626E9,0.05500802594388523],\"earned_surplus\":[4.672505348E9,0.0],\"undstrbtd_profit\":[3.931410757758E10,0.2739524650766714],\"minority_equity\":[1.51543162792E9,1.7376609111748644],\"total_holders_equity\":[5.904654439202E10,0.221122038981297],\"total_liab_and_holders_equity\":[9.62680525686E10,0.21524894260892752],\"lt_equity_invest\":[8.5616270905E8,0.6408517286922772],\"derivative_fnncl_liab\":[null,null],\"general_risk_provision\":[null,null],\"frgn_currency_convert_diff\":[null,null],\"goodwill\":[3.8049974304E8,0.4080111259789559],\"invest_property\":[null,null],\"interest_payable\":[null,null],\"treasury_stock\":[1.02318872304E9,-0.5001372355027347],\"othr_compre_income\":[-1.046740087E8,-0.1888177764466799],\"othr_equity_instruments\":[null,null],\"currency_funds\":[2.861721754923E10,0.07084237663210573],\"bills_receivable\":[1.21575756041E9,0.20236480596178832],\"account_receivable\":[2.616364877796E10,0.1335503025169427],\"pre_payment\":[6.4362719321E8,0.819944803398347],\"dividend_receivable\":[null,null],\"othr_receivables\":[null,null],\"inventory\":[1.9053058705E10,0.708334969467138],\"nca_due_within_one_year\":[1.28803466689E9,0.24327252449638573],\"othr_current_assets\":[1.14635400921E9,0.6147974685811431],\"current_assets_si\":[null,null],\"total_current_assets\":[8.017440561431E10,0.21315030703551258],\"lt_receivable\":[2.24930670258E9,0.1387966715993962],\"dev_expenditure\":[null,null],\"lt_deferred_expense\":[9.831671421E7,0.030964844643706236],\"othr_noncurrent_assets\":[9.8050248084E8,0.8060904508754352],\"noncurrent_assets_si\":[null,null],\"total_noncurrent_assets\":[1.609364695429E10,0.22581293050274795],\"st_loan\":[3.35156243669E9,-0.22767318592234465],\"bill_payable\":[6.4015956825E8,0.04874121668614618],\"accounts_payable\":[1.423968671474E10,0.45026885191296195],\"pre_receivable\":[null,null],\"dividend_payable\":[null,null],\"othr_payables\":[null,null],\"noncurrent_liab_due_in1y\":[3.61994826112E9,16.41932929124499],\"current_liab_si\":[null,null],\"total_current_liab\":[3.385007103476E10,0.3868583489412559],\"lt_loan\":[2.12868483674E9,-0.5563004053111297],\"lt_payable\":[null,null],\"special_payable\":[null,null],\"othr_non_current_liab\":[5.1159436152E8,-0.5856660991466901],\"noncurrent_liab_si\":[null,null],\"total_noncurrent_liab\":[3.37143714182E9,-0.47767426938228047],\"salable_financial_assets\":[null,null],\"othr_current_liab\":[7.6173580463E8,-0.5809286940814259],\"ar_and_br\":[2.737940633837E10,0.13643840485700268],\"contractual_assets\":[2.8286917903E8,1.2362918525099638],\"bp_and_ap\":[1.487984628299E10,0.4267676456978902],\"contract_liabilities\":[2.61187126668E9,0.9315497450231143],\"to_sale_asset\":[null,null],\"other_eq_ins_invest\":[null,null],\"other_illiquid_fnncl_assets\":[4.5672888816E8,-0.004004132840012091],\"fixed_asset_sum\":[6.37440617941E9,0.07135878512636074],\"fixed_assets_disposal\":[null,null],\"construction_in_process_sum\":[2.14641310492E9,0.7192442485538196],\"project_goods_and_material\":[null,null],\"productive_biological_assets\":[null,null],\"oil_and_gas_asset\":[null,null],\"to_sale_debt\":[null,null],\"lt_payable_sum\":[9260090.93,-0.8118338602806597],\"noncurrent_liab_di\":[2.738917496E8,0.420529409898882],\"perpetual_bond\":[null,null],\"special_reserve\":[null,null]}]},\"error_code\":0,\"error_description\":\"\"}";
        String name = Optional.ofNullable(JSONObject.parseObject(strBalanceResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(data -> data.getString("quote_name"))
                .orElse(StringUtils.EMPTY);
        List<JSONObject> jsonObjectList = Optional.ofNullable(JSONObject.parseObject(strBalanceResult))
                .map(jsonResult -> jsonResult.getJSONObject("data"))
                .map(data -> data.getString("list"))
                .map(strList -> JSON.parseArray(strList, JSONObject.class))
                .orElse(Lists.newArrayList()).stream()
                .map(report -> {
                    JSONObject resultObject =new JSONObject();
                    Long reportDateMillis = report.getLong("report_date");
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(new Date(reportDateMillis).toInstant(), ZoneId.systemDefault());
                    Integer year = localDateTime.getYear();
                    resultObject.put("year", year);
                    Integer month = localDateTime.getMonthValue();
                    resultObject.put("month", month);

                    for(String key : keyList){
                        if(report.containsKey(key)){
                            List<Double> valueList = JSON.parseArray(report.getString(key), Double.class);
                            if(CollectionUtils.isNotEmpty(valueList)){
                                resultObject.put(key, valueList.get(0));
                            }
                        }
                    }

                    return resultObject;
                })
                .collect(Collectors.toList());

        System.out.println("jsonObjectList: " + JSON.toJSONString(jsonObjectList));
    }

    @Test
    public void testGetBalanceResult(){
        String BALANCE_URL ="https://stock.xueqiu.com/v5/stock/finance/cn/balance.json";

        List<String> codes = Arrays.asList("SZ002001", "SZ002415", "SZ002508", "SH600486", "SZ002507");
        int count =5;

        for(String code : codes){
            String url =new StringBuilder().append(BALANCE_URL)
                    .append("?symbol=").append(code)
                    .append("&type=").append("all")
                    .append("&is_detail=").append("true")
                    .append("&count=").append(count)
                    .append("&timestamp=").append(StringUtils.EMPTY)
                    .toString();

            String strBalanceResult = CloseableHttpClientUtil.doGet(url, StringUtils.EMPTY);
            System.out.println("strBalanceResult: " + strBalanceResult);
        }

    }

    @Test
    public void checkData(){
        try {
            List<String> baseList =FileUtils.readLines(new File("G:/base.txt"), Charset.forName("UTF-8"));
            List<String> oriDataList =FileUtils.readLines(new File("G:/data.txt"), Charset.forName("UTF-8"));
            List<String> dataList = oriDataList.stream().collect(Collectors.toSet()).stream().collect(Collectors.toList());

            baseList.removeAll(dataList);
            if(!CollectionUtils.isEmpty(baseList)){
                FileUtils.writeLines(new File("G:/noData.txt"), baseList, true);
                System.out.print("list:"+ JSONObject.toJSONString(baseList));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void formatStockCode(){
        try {
            List<String> strList =FileUtils.readLines(new File("E:/stock_list_2_sz.txt"), Charset.forName("UTF-8"));
            if(CollectionUtils.isEmpty(strList)){
                return;
            }

            List<String> formatStrList =Lists.newArrayList();
            for(String str : strList){
                String[] split = StringUtils.split(str, ", ");
                if(split ==null || split.length <2){
                    continue;
                }

                String code =split[0];
                String name =split[1];
                String formatStr =new StringBuilder().append(code)
                        .append(",").append(name)
                        .toString();
                formatStrList.add(formatStr);
            }
            FileUtils.writeLines(new File("E:/stock_list_2_sz_f.txt"), formatStrList, true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
